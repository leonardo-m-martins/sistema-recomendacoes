package br.sistema_recomendacoes.dto;

import java.util.List;

import lombok.Data;

@Data
public class LivroRequestDTO {
    private String titulo;
    private Short ano;
    private String autor;
    private String pais_origem;
    private String descricao;
    private String capa;
    private Integer paginas;
    private String editora;
    private List<String> generos;
}
