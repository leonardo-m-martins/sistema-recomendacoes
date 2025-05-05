package br.sistema_recomendacoes.service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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

    public AvaliacaoResponseDTO findByLivroAndUsuario(Integer livro_id, Integer usuario_id){
        Avaliacao avaliacao = avaliacaoRepository.findByLivroAndUsuario(livro_id, usuario_id).orElseThrow( () -> new ResourceNotFoundException("Avaliação (livro_id: " + livro_id + ", usuario_id: " + usuario_id + ") não encontrado."));
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

    public AvaliacaoResponseDTO put(Integer id, AvaliacaoRequestDTO requestDTO){
        Avaliacao avaliacaoExistente = findById(id);
        Avaliacao avaliacaoAtualizada = AvaliacaoMapper.fromRequestDTO(requestDTO);

        // garantir que os IDs não são alterados
        avaliacaoAtualizada.setId(id);
        avaliacaoAtualizada.getLivro().setId(avaliacaoExistente.getLivro().getId());
        avaliacaoAtualizada.getUsuario().setId(avaliacaoExistente.getUsuario().getId());

        // salvar e retornar
        Avaliacao salvo = avaliacaoRepository.save(avaliacaoAtualizada);
        return AvaliacaoMapper.toResponseDTO(salvo);
    }

    public AvaliacaoResponseDTO patch(Integer id, Map<String, Object> updateMap){
        Avaliacao avaliacao = findById(id);
        PatchHelper.applyPatch(avaliacao, updateMap);
        Avaliacao salvo = avaliacaoRepository.save(avaliacao);
        return AvaliacaoMapper.toResponseDTO(salvo);
    }

    public void delete(Integer id){
        Avaliacao avaliacao = findById(id);
        avaliacaoRepository.delete(avaliacao);
    }

    public Iterable<Avaliacao> findByUsuario_id(Integer usuario_id){
        return avaliacaoRepository.findByUsuario_id(usuario_id);
    }

    public Set<Integer> findLivro_idByUsuario_id(Integer usuario_id){
        return avaliacaoRepository.findLivro_idByUsuario_id(usuario_id);
    }
}
