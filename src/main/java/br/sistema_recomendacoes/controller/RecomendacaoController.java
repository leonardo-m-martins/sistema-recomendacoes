package br.sistema_recomendacoes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.service.RecomendacaoService;

@RestController
@RequestMapping(path = "/recomendacao")
public class RecomendacaoController {

    @Autowired
    private RecomendacaoService recomendacaoService;

    @GetMapping(path = "/conteudo/{usuario_id}")
    public @ResponseBody ResponseEntity<List<LivroResponseDTO>> get(@PathVariable Integer usuario_id, @RequestParam(defaultValue = "10") int K){
        return ResponseEntity.ok().body(recomendacaoService.recomendar(usuario_id, K));
    }

    @GetMapping(path = "/colaborativa/{usuario_id}")
    public @ResponseBody ResponseEntity<List<LivroResponseDTO>> colaborativa(@PathVariable Integer usuario_id, @RequestParam(defaultValue = "10") int K){
        return ResponseEntity.ok().body(recomendacaoService.recomendarColaborativa(usuario_id, K));
    }

    @PostMapping(path = "/vetor-livro")
    public @ResponseBody ResponseEntity<Void> updateVetor_livro(){
        recomendacaoService.updateVetorLivros();
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/vetor-usuario")
    public @ResponseBody ResponseEntity<Void> updateVetor_usuario(){
        recomendacaoService.updateVetorUsuarios();
        return ResponseEntity.ok().build();
    }
}
