package br.sistema_recomendacoes.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivroResponseDTO {
    private Integer id;
    private String titulo;
    private String subtitulo;
    private Short primeira_data_publicacao;
    private Short data_publicacao;
    private String descricao;
    private String capa;
    private Integer paginas;
    private String editora;
    private List<AutorResponseDTO> autores;
    private List<GeneroResponseDTO> generos;
}
