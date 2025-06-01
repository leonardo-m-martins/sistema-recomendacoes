package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
    public void delete(Integer id){
        Usuario usuario = findById(id);
        usuarioRepository.delete(usuario);
    }

    // histórico
    @Transactional
    public List<LivroResponseDTO> historico(Integer id){
        Usuario usuario = findById(id);
        List<Avaliacao> avaliacaos = usuario.getAvaliacaos();
        List<LivroResponseDTO> dtos = new ArrayList<>(avaliacaos.size());
        for (Avaliacao avaliacao : avaliacaos) {
            dtos.add(LivroMapper.toResponseDTO(avaliacao.getLivro()));
        }
        return dtos;
    }

    public Usuario findById(Integer id){
        return usuarioRepository.findById((long) id).orElseThrow( () -> new ResourceNotFoundException("Usuário (id: " + id + ") não encontrado."));
    }

    @Transactional
    public Map<Integer, VetorLivro> getHistorico(Integer id_usuario){
        Map<Integer, VetorLivro> historico = new HashMap<>();
        Iterable<Avaliacao> avaliacaos = avaliacaoService.findByUsuario_id(id_usuario);
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
        return new User(usuario.getNome(), usuario.getSenha(), List.of()); // sem roles por enquanto
    }
}
