package br.sistema_recomendacoes.dto;

import java.util.List;

import lombok.Data;

@Data
public class LivroResponseDTO {
    private Integer id;
    private String titulo;
    private Short ano;
    private String autor;
    private String pais_origem;
    private String capa;
    private Integer paginas;
    private String editora;
    private List<GeneroResponseDTO> generos;
}
