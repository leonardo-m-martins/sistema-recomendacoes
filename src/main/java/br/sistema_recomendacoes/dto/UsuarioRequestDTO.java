package br.sistema_recomendacoes.dto;

import br.sistema_recomendacoes.model.Role;
import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String nome;
    private String senha;
    private String email;
    private Role role;
}
