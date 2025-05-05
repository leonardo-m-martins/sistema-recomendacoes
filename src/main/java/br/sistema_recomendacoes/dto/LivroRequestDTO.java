package br.sistema_recomendacoes.dto;

import java.util.List;

import lombok.Data;

/* EXEMPLO
{
    "titulo": "O Iluminado", 
    "descricao": "O romance, magistralmente levado ao cinema por Stanley Kubrick, [...]",
    "capa": "https://covers.openlibrary.org/b/id/14834369-L.jpg", 
    "paginas": 464, 
    "editora": "Suma", 
    "data_publicacao": null, 
    "autores": ["Stephen King"], 
    "primeira_data_publicacao": null, 
    "subtitulo": null, 
    "generos": ["Fiction", "horror fiction", ...]
}

*/

@Data
public class LivroRequestDTO {
    private String titulo;
    private String subtitulo;
    private Short primeira_data_publicacao;
    private Short data_publicacao;
    private String descricao;
    private String capa;
    private Integer paginas;
    private String editora;
    private List<AutorRequestDTO> autores;
    private List<GeneroRequestDTO> generos;
}
