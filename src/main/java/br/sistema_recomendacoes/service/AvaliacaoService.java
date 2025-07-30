package br.sistema_recomendacoes.service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.exception.UnauthorizedException;
import br.sistema_recomendacoes.mapper.LivroMapper;
import br.sistema_recomendacoes.model.TopLivros;
import br.sistema_recomendacoes.model.Usuario;
import br.sistema_recomendacoes.util.UserAuthenticator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.dto.AvaliacaoRequestDTO;
import br.sistema_recomendacoes.dto.AvaliacaoResponseDTO;
import br.sistema_recomendacoes.exception.ResourceNotFoundException;
import br.sistema_recomendacoes.mapper.AvaliacaoMapper;
import br.sistema_recomendacoes.model.Avaliacao;
import br.sistema_recomendacoes.repository.AvaliacaoRepository;
import br.sistema_recomendacoes.util.PatchHelper;

@Service
public class AvaliacaoService {
    
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    public Double calcAverageWithQuery(Integer livro_id){
        return avaliacaoRepository.calcAverage(livro_id);
    }

    @Transactional
    public AvaliacaoResponseDTO findByLivroAndUsuario(Integer livro_id, Integer usuario_id){
        Avaliacao avaliacao = avaliacaoRepository.findByLivroAndUsuario(livro_id, usuario_id).orElseThrow( () -> new ResourceNotFoundException("Avaliação (livro_id: " + livro_id + ", usuario_id: " + usuario_id + ") não encontrado."));

        Usuario usuario = avaliacao.getUsuario();
        if (!UserAuthenticator.authenticate(usuario)) throw new UnauthorizedException("Não é permitido ler, alterar ou deletar avaliações de outros usuários.");

        return AvaliacaoMapper.toResponseDTO(avaliacao);
    }

    private Avaliacao findById(Integer id){
        return avaliacaoRepository.findById((long) id).orElseThrow( () -> new ResourceNotFoundException("Avaliação (id: " + id + ") não encontrado."));
    }

    public AvaliacaoResponseDTO findByIdDto(Integer id){
        Avaliacao avaliacao = findById(id);
        return AvaliacaoMapper.toResponseDTO(avaliacao);
    }

    public AvaliacaoResponseDTO add(AvaliacaoRequestDTO requestDTO){
        Avaliacao avaliacao = AvaliacaoMapper.fromRequestDTO(requestDTO);
        Optional<Avaliacao> avaliacaoOptional = avaliacaoRepository.findByLivroAndUsuario(requestDTO.getLivro_id(), requestDTO.getUsuario_id());
        boolean isPresent = avaliacaoOptional.isPresent();
        if(isPresent){
            throw new RuntimeException();
        }
        else{
            Avaliacao salvo = avaliacaoRepository.save(avaliacao);
            return AvaliacaoMapper.toResponseDTO(salvo);
        }
    }

    @Transactional
    public AvaliacaoResponseDTO put(Integer id, AvaliacaoRequestDTO requestDTO){
        Avaliacao avaliacaoExistente = findById(id);

        Usuario usuario = avaliacaoExistente.getUsuario();
        if (!UserAuthenticator.authenticate(usuario)) throw new UnauthorizedException("Não é permitido ler, alterar ou deletar avaliações de outros usuários.");

        Avaliacao avaliacaoAtualizada = AvaliacaoMapper.fromRequestDTO(requestDTO);

        // garantir que os IDs não são alterados
        avaliacaoAtualizada.setId(id);
        avaliacaoAtualizada.getLivro().setId(avaliacaoExistente.getLivro().getId());
        avaliacaoAtualizada.getUsuario().setId(avaliacaoExistente.getUsuario().getId());

        // salvar e retornar
        Avaliacao salvo = avaliacaoRepository.save(avaliacaoAtualizada);
        return AvaliacaoMapper.toResponseDTO(salvo);
    }

    @Transactional
    public AvaliacaoResponseDTO patch(Integer id, Map<String, Object> updateMap){
        Avaliacao avaliacao = findById(id);

        Usuario usuario = avaliacao.getUsuario();
        if (!UserAuthenticator.authenticate(usuario)) throw new UnauthorizedException("Não é permitido ler, alterar ou deletar avaliações de outros usuários.");

        PatchHelper.applyPatch(avaliacao, updateMap);
        Avaliacao salvo = avaliacaoRepository.save(avaliacao);
        return AvaliacaoMapper.toResponseDTO(salvo);
    }

    @Transactional
    public void delete(Integer id){
        Avaliacao avaliacao = findById(id);
        Usuario usuario = avaliacao.getUsuario();
        if (!UserAuthenticator.authenticate(usuario)) throw new UnauthorizedException("Não é permitido ler, alterar ou deletar avaliações de outros usuários.");

        avaliacaoRepository.delete(avaliacao);
    }

    public Iterable<Avaliacao> findByUsuario_id(Integer usuario_id){
        return avaliacaoRepository.findByUsuario_id(usuario_id);
    }

    public Set<Integer> findLivro_idByUsuario_id(Integer usuario_id){
        return avaliacaoRepository.findLivro_idByUsuario_id(usuario_id);
    }

    @Transactional
    public Page<LivroResponseDTO> findTopLivros(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<TopLivros> topLivros = avaliacaoRepository.findTopLivros(pageable);

        return topLivros.map(TopLivros::getLivro).map(LivroMapper::toResponseDTO);
    }
}
