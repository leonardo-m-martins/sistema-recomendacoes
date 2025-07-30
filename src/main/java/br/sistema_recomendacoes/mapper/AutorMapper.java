package br.sistema_recomendacoes.mapper;

import br.sistema_recomendacoes.dto.AutorRequestDTO;
import br.sistema_recomendacoes.dto.AutorResponseDTO;
import br.sistema_recomendacoes.model.Autor;
import org.apache.commons.text.StringEscapeUtils;

public class AutorMapper {
    public static Autor fromRequestDTO(AutorRequestDTO dto){
        return new Autor(dto.getNome());
    }

    public static AutorResponseDTO toResponseDTO(Autor autor){
        AutorResponseDTO dto = new AutorResponseDTO();
        dto.setId(autor.getId());
        dto.setNome(StringEscapeUtils.unescapeJava(autor.getNome()));
        dto.setNum_livros(autor.getNum_livros());
        return dto;
    }
}
