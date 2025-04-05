package br.sistema_recomendacoes.mapper;

import br.sistema_recomendacoes.dto.UsuarioRequestDTO;
import br.sistema_recomendacoes.dto.UsuarioResponseDTO;
import br.sistema_recomendacoes.model.Usuario;

public class UsuarioMapper {
    public static Usuario fromRequestDTO(UsuarioRequestDTO dto){
        Usuario usuario = new Usuario(dto.getNome(), dto.getSenha());
        return usuario;
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario){
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        return dto;
    }
}
