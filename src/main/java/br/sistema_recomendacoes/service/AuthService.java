package br.sistema_recomendacoes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.dto.UsuarioRequestDTO;
import br.sistema_recomendacoes.dto.UsuarioResponseDTO;
import br.sistema_recomendacoes.exception.BadRequestException;
import br.sistema_recomendacoes.exception.UnauthorizedException;
import br.sistema_recomendacoes.mapper.UsuarioMapper;
import br.sistema_recomendacoes.model.Usuario;
import br.sistema_recomendacoes.repository.UsuarioRepository;
import br.sistema_recomendacoes.security.JwtUtil;

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
    public String login(UsuarioRequestDTO requestDTO){
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

        return jwtUtil.generateToken(usuario.getNome());
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
