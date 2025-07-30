package br.sistema_recomendacoes.mapper;

import br.sistema_recomendacoes.dto.GeneroRequestDTO;
import br.sistema_recomendacoes.dto.GeneroResponseDTO;
import br.sistema_recomendacoes.model.Genero;
import org.apache.commons.text.StringEscapeUtils;

public class GeneroMapper {
    public static Genero fromRequestDTO(GeneroRequestDTO dto){
        return new Genero(dto.getNome());
    }

    public static GeneroResponseDTO toResponseDTO(Genero genero){
        GeneroResponseDTO dto = new GeneroResponseDTO();
        dto.setId(genero.getId());
        dto.setNome(StringEscapeUtils.unescapeJava(genero.getNome()));
        dto.setNum_livros(genero.getNum_livros());
        return dto;
    }
}
