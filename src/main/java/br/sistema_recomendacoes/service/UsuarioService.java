package br.sistema_recomendacoes.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.dto.UsuarioRequestDTO;
import br.sistema_recomendacoes.dto.UsuarioResponseDTO;
import br.sistema_recomendacoes.mapper.UsuarioMapper;
import br.sistema_recomendacoes.model.Usuario;
import br.sistema_recomendacoes.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // create
    public UsuarioResponseDTO add(UsuarioRequestDTO requestDTO) {
        Usuario usuario = UsuarioMapper.fromRequestDTO(requestDTO);
        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDTO(salvo);
    }

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
        Usuario usuario = usuarioRepository.findById((long) id).orElseThrow();
        return UsuarioMapper.toResponseDTO(usuario);
    }

    // put
    public UsuarioResponseDTO put(Integer id, UsuarioRequestDTO requestDTO){
        findById(id); // verifica se o usuario existe no banco de dados
        Usuario novoUsuario = UsuarioMapper.fromRequestDTO(requestDTO);
        novoUsuario.setId(id); // garante que o id é o mesmo
        Usuario salvo = usuarioRepository.save(novoUsuario);
        return UsuarioMapper.toResponseDTO(salvo);
    }

    // patch
    public UsuarioResponseDTO patch(Integer id, Map<String, Object> updateMap){
        Usuario usuario = findById(id);
        PatchHelper.applyPatch(usuario, updateMap);
        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDTO(salvo);
    }

    // delete
    public void delete(Integer id){
        Usuario usuario = findById(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario findById(Integer id){
        return usuarioRepository.findById((long) id).orElseThrow();
    }
}
