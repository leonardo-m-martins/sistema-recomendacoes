package br.sistema_recomendacoes.dto;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String nome;
    private String senha;
    private String email;
}
