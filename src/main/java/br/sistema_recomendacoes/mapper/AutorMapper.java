package br.sistema_recomendacoes.mapper;

import br.sistema_recomendacoes.dto.AutorRequestDTO;
import br.sistema_recomendacoes.dto.AutorResponseDTO;
import br.sistema_recomendacoes.model.Autor;

public class AutorMapper {
    public static Autor fromRequestDTO(AutorRequestDTO dto){
        Autor autor = new Autor(dto.getNome());
        return autor;
    }

    public static AutorResponseDTO toResponseDTO(Autor autor){
        AutorResponseDTO dto = new AutorResponseDTO();
        dto.setId(autor.getId());
        dto.setNome(autor.getNome());
        dto.setNum_livros(autor.getNum_livros());
        return dto;
    }
}
