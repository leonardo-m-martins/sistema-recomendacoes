package br.sistema_recomendacoes.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import br.sistema_recomendacoes.dto.AutorRequestDTO;
import br.sistema_recomendacoes.dto.AutorResponseDTO;
import br.sistema_recomendacoes.dto.GeneroRequestDTO;
import br.sistema_recomendacoes.dto.GeneroResponseDTO;
import br.sistema_recomendacoes.dto.LivroRequestDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.model.Autor;
import br.sistema_recomendacoes.model.Genero;
import br.sistema_recomendacoes.model.Livro;

public class LivroMapper {
    public static Livro fromRequestDTO(LivroRequestDTO dto) {
        Livro livro = Livro.builder()
            .titulo(dto.getTitulo())
            .descricao(dto.getDescricao())
            .primeira_data_publicacao(dto.getPrimeira_data_publicacao())
            .data_publicacao(dto.getData_publicacao())
            .subtitulo(dto.getSubtitulo())
            .capa(dto.getCapa())
            .paginas(dto.getPaginas())
            .editora(dto.getEditora())
            .build();

        List<Genero> generos = new ArrayList<>();
        for (GeneroRequestDTO gDto : dto.getGeneros()) {
            Genero genero = GeneroMapper.fromRequestDTO(gDto);
            generos.add(genero);
        }
        livro.setGeneros(generos);

        List<Autor> autores = new ArrayList<>();
        for (AutorRequestDTO aDto : dto.getAutores()) {
            Autor autor = AutorMapper.fromRequestDTO(aDto);
            autores.add(autor);
        }
        livro.setAutores(autores);

        return livro;
    }

    @Transactional
    public static LivroResponseDTO toResponseDTO(Livro livro) {
        LivroResponseDTO dto = new LivroResponseDTO();
        dto.setId(livro.getId());
        dto.setTitulo(livro.getTitulo());
        dto.setDescricao(livro.getDescricao());
        dto.setPrimeira_data_publicacao(livro.getPrimeira_data_publicacao());
        dto.setData_publicacao(livro.getData_publicacao());
        dto.setSubtitulo(livro.getSubtitulo());
        dto.setCapa(livro.getCapa());
        dto.setPaginas(livro.getPaginas());
        dto.setEditora(livro.getEditora());

        List<Genero> livroGeneros = livro.getGeneros();
        List<GeneroResponseDTO> generosDTO = new ArrayList<>();
        if (!livroGeneros.isEmpty())
        for (Genero genero : livroGeneros) {
            GeneroResponseDTO generoDTO = GeneroMapper.toResponseDTO(genero);
            generosDTO.add(generoDTO);
        }
        dto.setGeneros(generosDTO);

        List<Autor> livroAutores = livro.getAutores();
        List<AutorResponseDTO> autorResponseDTOs = new ArrayList<>();
        if (!livroAutores.isEmpty())
        for (Autor autor : livroAutores) {
            AutorResponseDTO autorResponseDTO = AutorMapper.toResponseDTO(autor);
            autorResponseDTOs.add(autorResponseDTO);
        }
        dto.setAutores(autorResponseDTOs);
        return dto;
    }
}
