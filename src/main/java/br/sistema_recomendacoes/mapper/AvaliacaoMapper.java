package br.sistema_recomendacoes.mapper;

import br.sistema_recomendacoes.dto.AvaliacaoRequestDTO;
import br.sistema_recomendacoes.dto.AvaliacaoResponseDTO;
import br.sistema_recomendacoes.model.Avaliacao;
import br.sistema_recomendacoes.model.Livro;
import br.sistema_recomendacoes.model.Usuario;

public class AvaliacaoMapper {
    public static Avaliacao fromRequestDTO(AvaliacaoRequestDTO dto){
        Avaliacao avaliacao = new Avaliacao();
        
        avaliacao.setNota(dto.getNota());
        
        Livro livro = new Livro();
        livro.setId(dto.getLivro_id());
        avaliacao.setLivro(livro);

        Usuario usuario = new Usuario(null, null);
        usuario.setId(dto.getUsuario_id());
        avaliacao.setUsuario(usuario);

        return avaliacao;
    }

    public static AvaliacaoResponseDTO toResponseDTO(Avaliacao avaliacao){
        AvaliacaoResponseDTO dto = new AvaliacaoResponseDTO();
        dto.setId(avaliacao.getId());
        dto.setLivro_id(avaliacao.getLivro().getId());
        dto.setUsuario_id(avaliacao.getUsuario().getId());
        dto.setNota(avaliacao.getNota());
        return dto;
    }
}
