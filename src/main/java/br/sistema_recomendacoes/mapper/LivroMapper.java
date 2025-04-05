package br.sistema_recomendacoes.mapper;

import java.util.ArrayList;
import java.util.List;

import br.sistema_recomendacoes.dto.GeneroRequestDTO;
import br.sistema_recomendacoes.dto.GeneroResponseDTO;
import br.sistema_recomendacoes.dto.LivroRequestDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.model.Genero;
import br.sistema_recomendacoes.model.Livro;

public class LivroMapper {
    public static Livro fromRequestDTO(LivroRequestDTO dto) {
        Livro livro = new Livro(dto.getTitulo(),dto.getPais_origem(),dto.getAno(),
            dto.getDescricao(),dto.getCapa(),dto.getAutor(),dto.getPaginas(),dto.getEditora());
        List<Genero> generos = new ArrayList<>();
        for (String nome : dto.getGeneros()) {
            GeneroRequestDTO gDto = new GeneroRequestDTO();
            gDto.setNome(nome);
            Genero genero = GeneroMapper.fromRequestDTO(gDto);
            generos.add(genero);
        }
        livro.setGeneros(generos);
        return livro;
    }

    public static LivroResponseDTO toResponseDTO(Livro livro) {
        LivroResponseDTO dto = new LivroResponseDTO();
        dto.setAno(livro.getAno());
        dto.setAutor(livro.getAutor());
        dto.setCapa(livro.getCapa());
        dto.setEditora(livro.getEditora());
        dto.setId(livro.getId());
        dto.setPaginas(livro.getPaginas());
        dto.setPais_origem(livro.getPais_origem());
        dto.setTitulo(livro.getTitulo());
        List<Genero> livroGeneros = livro.getGeneros();
        List<GeneroResponseDTO> generosDTO = new ArrayList<>();
        for (Genero genero : livroGeneros) {
            GeneroResponseDTO generoDTO = GeneroMapper.toResponseDTO(genero);
            generosDTO.add(generoDTO);
        }
        dto.setGeneros(generosDTO);
        return dto;
    }
}
