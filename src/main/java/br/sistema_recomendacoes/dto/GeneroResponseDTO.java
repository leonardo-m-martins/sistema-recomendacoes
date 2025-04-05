package br.sistema_recomendacoes.dto;

import lombok.Data;

@Data
public class GeneroResponseDTO {
    private Integer id;
    private String nome;
    private Integer num_livros;
}
