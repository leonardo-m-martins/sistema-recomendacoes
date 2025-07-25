package br.sistema_recomendacoes.mapper;

import br.sistema_recomendacoes.dto.UsuarioRequestDTO;
import br.sistema_recomendacoes.dto.UsuarioResponseDTO;
import br.sistema_recomendacoes.model.Usuario;

public class UsuarioMapper {
    public static Usuario fromRequestDTO(UsuarioRequestDTO dto){
        return new Usuario(dto.getNome(), dto.getSenha(), dto.getEmail(), dto.getRole());
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario){
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        return dto;
    }
}
