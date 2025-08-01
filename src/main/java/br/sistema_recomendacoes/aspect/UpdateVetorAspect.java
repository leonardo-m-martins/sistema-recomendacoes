package br.sistema_recomendacoes.aspect;

import br.sistema_recomendacoes.dto.AvaliacaoResponseDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.service.RecomendacaoService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UpdateVetorAspect {

    @Autowired
    private RecomendacaoService recomendacaoService;

//    Depois de uma avaliação ser criada, alterada, ou deletada, atualizar os vetores de usuário.
    @AfterReturning(
            value = "execution(public * br.sistema_recomendacoes.controller.AvaliacaoController..*(..)) && " +
                    "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
                    "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
                    "@annotation(org.springframework.web.bind.annotation.PatchMapping))",
            returning = "responseEntity"

    )
    public void afterAvaliacaoNotGet(ResponseEntity<AvaliacaoResponseDTO> responseEntity) {
        AvaliacaoResponseDTO responseDTO = responseEntity.getBody();
        int usuarioId = responseDTO.getUsuario_id();
        recomendacaoService.updateVetorUsuario(usuarioId);
    }

    @AfterReturning(
            value = "execution(public * br.sistema_recomendacoes.service.AvaliacaoService.delete(..))",
            returning = "usuarioId"
    )
    public void afterAvaliacaoDelete(int usuarioId) {
        recomendacaoService.updateVetorUsuario(usuarioId);
    }


//    Depois de um livro ser adicionado ou alterado, atualizar os vetores.
    @AfterReturning(
            value = "execution(public * br.sistema_recomendacoes.controller.LivroController..*(..)) &&" +
                    "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
                    "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
                    "@annotation(org.springframework.web.bind.annotation.PatchMapping))",
            returning = "responseEntity"
    )
    public void afterLivroNotGet(ResponseEntity<LivroResponseDTO> responseEntity) {
        LivroResponseDTO livroResponseDTO = responseEntity.getBody();
        int livroId = livroResponseDTO.getId();
        recomendacaoService.updateVetorLivro(livroId);
    }

    @AfterReturning(
            value = "execution(public * br.sistema_recomendacoes.service.LivroService.delete(..))",
            returning = "livroId"
    )
    public void afterLivroDelete(int livroId) {
        recomendacaoService.removeVetorLivro(livroId);
    }


    @AfterReturning(
            value = "execution(public * br.sistema_recomendacoes.service.UsuarioService.delete(..))",
            returning = "usuarioId"
    )
    public void afterUsuarioDelete(int usuarioId) {
        recomendacaoService.removeVetorUsuario(usuarioId);
    }


}
