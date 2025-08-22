package br.sistema_recomendacoes.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.sistema_recomendacoes.dto.AutorRequestDTO;
import br.sistema_recomendacoes.dto.AutorResponseDTO;
import br.sistema_recomendacoes.dto.GeneroRequestDTO;
import br.sistema_recomendacoes.dto.GeneroResponseDTO;
import br.sistema_recomendacoes.dto.LivroRequestDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.model.Autor;
import br.sistema_recomendacoes.model.Genero;
import br.sistema_recomendacoes.model.Livro;
import org.apache.commons.text.StringEscapeUtils;

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

    public static Livro fromRequestDTO(LivroRequestDTO dto, Map<String, Integer> generosMap, Map<String, Integer> autoresMap) {
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

        livro.setGeneros(dto.getGeneros().stream()
            .distinct()
            .map(g -> {
                Integer id = generosMap.get(g.getNome());
                return (id != null) ? new Genero(id) : null;
            })
            .filter(Objects::nonNull)
            .toList());

        livro.setAutores(dto.getAutores().stream()
            .distinct()
            .map(a -> {
                Integer id = autoresMap.get(a.getNome());
                return (id != null) ? new Autor(id) : null;
            })
            .filter(Objects::nonNull)
            .toList());

        return livro;
    }

    public static LivroResponseDTO toResponseDTO(Livro livro) {
        LivroResponseDTO dto = new LivroResponseDTO();
        dtoSetAttributes(dto, livro);

        List<Genero> livroGeneros = livro.getGeneros();
        List<GeneroResponseDTO> generosDTO = livroGeneros.stream()
                .map(GeneroMapper::toResponseDTO)
                .toList();
        dto.setGeneros(generosDTO);

        List<Autor> livroAutores = livro.getAutores();
        List<AutorResponseDTO> autorResponseDTOs = livroAutores.stream()
                .map(AutorMapper::toResponseDTO)
                .toList();
        dto.setAutores(autorResponseDTOs);

        return dto;
    }

    public static LivroResponseDTO toLazyResponseDTO(Livro livro) {
        LivroResponseDTO dto = new LivroResponseDTO();
        dtoSetAttributes(dto, livro);

        return dto;
    }

    private static void dtoSetAttributes(LivroResponseDTO dto, Livro livro) {
        dto.setId(livro.getId());
        dto.setTitulo(StringEscapeUtils.unescapeJava(livro.getTitulo()));
        dto.setDescricao(StringEscapeUtils.unescapeJava(livro.getDescricao()));
        dto.setPrimeira_data_publicacao(livro.getPrimeira_data_publicacao());
        dto.setData_publicacao(livro.getData_publicacao());
        dto.setSubtitulo(StringEscapeUtils.unescapeJava(livro.getSubtitulo()));
        dto.setCapa(livro.getCapa());
        dto.setPaginas(livro.getPaginas());
        dto.setEditora(StringEscapeUtils.unescapeJava(livro.getEditora()));
    }
}
