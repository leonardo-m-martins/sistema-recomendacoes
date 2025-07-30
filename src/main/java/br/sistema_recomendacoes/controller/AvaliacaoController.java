package br.sistema_recomendacoes.controller;

import java.util.Map;

import br.sistema_recomendacoes.dto.LivroResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.sistema_recomendacoes.dto.AvaliacaoRequestDTO;
import br.sistema_recomendacoes.dto.AvaliacaoResponseDTO;
import br.sistema_recomendacoes.service.AvaliacaoService;

@RestController
@RequestMapping(path = "/avaliacao")
public class AvaliacaoController {
    
    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping("/")
    public @ResponseBody ResponseEntity<AvaliacaoResponseDTO> add(@RequestBody AvaliacaoRequestDTO avaliacao){
        AvaliacaoResponseDTO salvo = avaliacaoService.add(avaliacao);
        return ResponseEntity.ok().body(salvo);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<AvaliacaoResponseDTO> findById(@PathVariable Integer id){
        AvaliacaoResponseDTO avaliacao = avaliacaoService.findByIdDto(id);
        return ResponseEntity.ok().body(avaliacao);
    }

    @PutMapping("/{id}")
    public @ResponseBody ResponseEntity<AvaliacaoResponseDTO> put(@PathVariable Integer id, @RequestBody AvaliacaoRequestDTO avaliacao){
        AvaliacaoResponseDTO salvo = avaliacaoService.put(id, avaliacao);
        return ResponseEntity.ok().body(salvo);
    }

    @PatchMapping("/{id}")
    public @ResponseBody ResponseEntity<AvaliacaoResponseDTO> patch(@PathVariable Integer id, @RequestBody Map<String, Object> updateMap){
        AvaliacaoResponseDTO salvo = avaliacaoService.patch(id, updateMap);
        return ResponseEntity.ok().body(salvo);
    }

    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<Void> delete(@PathVariable Integer id){
        avaliacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/livro-media/{livroId}")
    public @ResponseBody ResponseEntity<Double> calcAverage(@PathVariable Integer livroId){
        Double avaliacaoAverage = avaliacaoService.calcAverageWithQuery(livroId);
        return ResponseEntity.ok().body(avaliacaoAverage);
    }

    @GetMapping("/livro-usuario")
    public @ResponseBody ResponseEntity<AvaliacaoResponseDTO> findByLivroAndUsuario(@RequestParam Integer livroId, @RequestParam Integer usuarioId){
        AvaliacaoResponseDTO salvo = avaliacaoService.findByLivroAndUsuario(livroId, usuarioId);
        return ResponseEntity.ok().body(salvo);
    }

//    Encontra os livros com maior soma de notas,
//    por exemplo: um livro com duas notas 5 tem somaNotas 10, um livro com 9 notas 1 tem somaNotas 9,
//    o primeiro será o que tem somaNotas maior.
    @GetMapping("/top-livros")
    public @ResponseBody ResponseEntity<Page<LivroResponseDTO>> findTopLivros(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "20") Integer size){
        Page<LivroResponseDTO> responseDTOS = avaliacaoService.findTopLivros(page, size);
        return ResponseEntity.ok().body(responseDTOS);
    }
}
