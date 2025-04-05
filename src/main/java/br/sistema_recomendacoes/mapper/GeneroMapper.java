package br.sistema_recomendacoes.mapper;

import br.sistema_recomendacoes.dto.GeneroRequestDTO;
import br.sistema_recomendacoes.dto.GeneroResponseDTO;
import br.sistema_recomendacoes.model.Genero;

public class GeneroMapper {
    public static Genero fromRequestDTO(GeneroRequestDTO dto){
        Genero genero = new Genero(dto.getNome());
        return genero;
    }

    public static GeneroResponseDTO toResponseDTO(Genero genero){
        GeneroResponseDTO dto = new GeneroResponseDTO();
        dto.setId(genero.getId());
        dto.setNome(genero.getNome());
        dto.setNum_livros(genero.getNum_livros());
        return dto;
    }
}
