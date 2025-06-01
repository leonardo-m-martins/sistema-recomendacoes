package br.sistema_recomendacoes.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.dto.UsuarioRequestDTO;
import br.sistema_recomendacoes.dto.UsuarioResponseDTO;
import br.sistema_recomendacoes.exception.BadRequestException;
import br.sistema_recomendacoes.exception.ResourceNotFoundException;
import br.sistema_recomendacoes.exception.UnauthorizedException;
import br.sistema_recomendacoes.mapper.UsuarioMapper;
import br.sistema_recomendacoes.model.Usuario;
import br.sistema_recomendacoes.repository.UsuarioRepository;
import br.sistema_recomendacoes.security.JwtUtil;
import br.sistema_recomendacoes.util.PatchHelper;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // create
    public UsuarioResponseDTO add(UsuarioRequestDTO requestDTO) {
        requestDTO.setSenha(passwordEncoder.encode(requestDTO.getSenha()));
        Usuario usuario = UsuarioMapper.fromRequestDTO(requestDTO);
        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDTO(salvo);
    }

    // login
    public Map<String, Object> login(UsuarioRequestDTO requestDTO){
        Usuario usuario = null;

        if (requestDTO.getNome() != null && !requestDTO.getNome().isBlank()) {
            usuario = usuarioRepository.findByNome(requestDTO.getNome());
        } else if (requestDTO.getEmail() != null && !requestDTO.getEmail().isBlank()) {
            usuario = usuarioRepository.findByEmail(requestDTO.getEmail());
        } else {
            throw new BadRequestException("Informe nome ou e-mail para login.");
        }

        if (usuario == null || !passwordEncoder.matches(requestDTO.getSenha(), usuario.getSenha())) {
            throw new UnauthorizedException("Usuário ou senha inválidos.");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("usuario", UsuarioMapper.toResponseDTO(usuario));
        map.put("token", jwtUtil.generateToken(usuario.getNome()));

        return map;
    }

    // put
    public UsuarioResponseDTO put(Integer id, UsuarioRequestDTO requestDTO){
        encodeSenha(requestDTO);
        usuarioRepository.findById((long) id).orElseThrow(() -> new ResourceNotFoundException("Usuário (id: " + id + ") não encontrado.")); // verifica se o usuario existe no banco de dados
        Usuario novoUsuario = UsuarioMapper.fromRequestDTO(requestDTO);
        novoUsuario.setId(id); // garante que o id é o mesmo
        Usuario salvo = usuarioRepository.save(novoUsuario);
        return UsuarioMapper.toResponseDTO(salvo);
    }

    // patch
    public UsuarioResponseDTO patch(Integer id, Map<String, Object> updateMap){
        
        Usuario usuario = usuarioRepository.findById((long) id).orElseThrow(() -> new ResourceNotFoundException("Usuário (id: " + id + ") não encontrado.")); // verifica se o usuario existe no banco de dados
        if(updateMap.containsKey("senha")){
            String senha = (String) updateMap.get("senha");
            usuario.setSenha(encodeSenha(senha));
            updateMap.remove("senha");
        }
        PatchHelper.applyPatch(usuario, updateMap);
        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDTO(salvo);
    }

    public void encodeSenha(UsuarioRequestDTO dto){
        if (dto.getSenha() != null) dto.setSenha(passwordEncoder.encode(dto.getSenha()));
        else throw new BadRequestException("Senha não pode ser vazia.");
    }

    public String encodeSenha(String senha){
        if (senha != null) return passwordEncoder.encode(senha);
        else throw new BadRequestException("Senha não pode ser vazia.");
    }
}
