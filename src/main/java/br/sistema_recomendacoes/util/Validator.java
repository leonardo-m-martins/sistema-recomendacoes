package br.sistema_recomendacoes.util;

import java.text.Normalizer;

import br.sistema_recomendacoes.dto.AutorRequestDTO;
import br.sistema_recomendacoes.dto.GeneroRequestDTO;
import br.sistema_recomendacoes.model.Livro;

public class Validator {
    // false: impossível validar
    public static boolean validate(Livro livro){
        if (livro.getTitulo() == null) {
            System.out.println("Título null: " + livro.toString());
            return false;
        }
        if (livro.getCapa() != null && livro.getCapa().length() > 500){
            livro.setCapa(null);
        }
        truncString(livro);
        return true;
    }

    public static void truncString(Livro livro){
        String editora = livro.getEditora(); 
        String subtitulo = livro.getSubtitulo();
        String titulo = livro.getTitulo();
        String descricao = livro.getDescricao();

        if (editora != null && editora.length() > 255) {
            livro.setEditora(editora.substring(0, 250) + "TRUNC");
        }
        if (subtitulo != null && subtitulo.length() > 255) {
            livro.setSubtitulo(subtitulo.substring(0, 250) + "TRUNC");
        }
        if (titulo != null && titulo.length() > 255) {
            livro.setTitulo(titulo.substring(0, 250) + "TRUNC");
        }
        if (descricao != null && descricao.length() > 16_000){
            livro.setDescricao(descricao.substring(0, 16_000) + "TRUNC");
        }
    }

    public static String truncString(String s, int size){
        if (s != null && s.length() > size){
            return s.substring(0, (size - 5)) + "TRUNC";
        }
        else return s;
    }

    public static boolean validate(GeneroRequestDTO dto) {
        if (dto == null || dto.getNome() == null) {
            return false;
        }

        String nome = Normalizer.normalize(dto.getNome().strip(), Normalizer.Form.NFC);
        nome = truncString(nome, 255);
        
        // Opcional: remover múltiplos espaços internos, converter para forma canônica etc.
        // nome = nome.replaceAll("\\s+", " "); // se quiser padronizar espaços internos

        dto.setNome(nome); // sobrescreve com nome limpo

        return !nome.isEmpty(); // retorna false se ficou vazio
    }

    public static boolean validate(AutorRequestDTO dto) {
        if (dto == null || dto.getNome() == null) {
            return false;
        }

        String nome = Normalizer.normalize(dto.getNome().strip(), Normalizer.Form.NFC);
        nome = truncString(nome, 255);

        // Opcional: remover múltiplos espaços internos, converter para forma canônica etc.
        // nome = nome.replaceAll("\\s+", " "); // se quiser padronizar espaços internos

        dto.setNome(nome); // sobrescreve com nome limpo

        return !nome.isEmpty(); // retorna false se ficou vazio
    }
}
