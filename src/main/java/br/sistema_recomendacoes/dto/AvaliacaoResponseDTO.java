package br.sistema_recomendacoes.dto;

import lombok.Data;

@Data
public class AvaliacaoResponseDTO {
    private Integer id;
    private Integer livro_id;
    private Integer usuario_id;
    private Integer nota;
}
