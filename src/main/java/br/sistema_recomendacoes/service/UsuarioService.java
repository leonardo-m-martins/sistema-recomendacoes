package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.sistema_recomendacoes.exception.UnauthorizedException;
import br.sistema_recomendacoes.model.Livro;
import br.sistema_recomendacoes.util.UserAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.dto.UsuarioResponseDTO;
import br.sistema_recomendacoes.exception.ResourceNotFoundException;
import br.sistema_recomendacoes.mapper.LivroMapper;
import br.sistema_recomendacoes.mapper.UsuarioMapper;
import br.sistema_recomendacoes.model.Avaliacao;
import br.sistema_recomendacoes.model.Usuario;
import br.sistema_recomendacoes.model.VetorLivro;
import br.sistema_recomendacoes.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AvaliacaoService avaliacaoService;

    // read all
    public List<UsuarioResponseDTO> getAllUsuarios() {
        Iterable<Usuario> usuarioIterable = usuarioRepository.findAll();
        List<UsuarioResponseDTO> responseDTOs = new ArrayList<>();
        for (Usuario usuario : usuarioIterable) {
            responseDTOs.add(UsuarioMapper.toResponseDTO(usuario));
        }
        return responseDTOs;
    }

    // read
    public UsuarioResponseDTO findByIdDto(Integer id){
        Usuario usuario = findById(id);
        return UsuarioMapper.toResponseDTO(usuario);
    }

    // delete
    public int delete(Integer id){
        Usuario usuario = findById(id);
        usuarioRepository.delete(usuario);

        return id;
    }

    // histórico
    @Transactional
    public Page<LivroResponseDTO> historico(Integer usuarioId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        Page<Livro> historicoLivros = usuarioRepository.getHistorico(usuarioId, pageable);
        return historicoLivros.map(LivroMapper::toLazyResponseDTO);
    }

    public Usuario findById(Integer id){
        return usuarioRepository.findById((long) id).orElseThrow( () -> new ResourceNotFoundException("Usuário (id: " + id + ") não encontrado."));
    }

    @Transactional
    public Map<Integer, VetorLivro> getHistorico(Integer idUsuario){
        Usuario usuario = findById(idUsuario);
        if (!UserAuthenticator.authenticate(usuario)) throw new UnauthorizedException("Não é permitido acessar o histórico de outros usuários.");
        Map<Integer, VetorLivro> historico = new HashMap<>();
        Iterable<Avaliacao> avaliacaos = avaliacaoService.findByUsuario_id(idUsuario);
        for (Avaliacao avaliacao : avaliacaos) {
            historico.put(avaliacao.getNotaOrPadrao(), new VetorLivro(avaliacao.getLivro()));
        }
        return historico;
    }

    public int count(){
        return (int) usuarioRepository.count();
    }

    public List<Usuario> findAllList(){
        return usuarioRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findByNome(username);
        if (usuario == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        return new User(
                usuario.getNome(),
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()))
        );
    }
}
