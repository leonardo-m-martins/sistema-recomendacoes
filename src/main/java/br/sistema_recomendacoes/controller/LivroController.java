package br.sistema_recomendacoes.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.sistema_recomendacoes.dto.LivroRequestDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.service.LivroService;

@RestController
@RequestMapping(path = "/api/livro")
public class LivroController {
    
    @Autowired
    private LivroService livroService;

    @PostMapping("/")
    public @ResponseBody ResponseEntity<LivroResponseDTO> add(@RequestBody LivroRequestDTO livro){
        LivroResponseDTO salvo = livroService.add(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/")
    public @ResponseBody ResponseEntity<List<LivroResponseDTO>> findAll(){
        List<LivroResponseDTO> livrosIterable = livroService.findAll();
        return ResponseEntity.ok().body(livrosIterable);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<LivroResponseDTO> findById(@PathVariable Integer id){
        LivroResponseDTO livro = livroService.findByIdDto(id);
        return ResponseEntity.ok().body(livro);
    }

    @PutMapping("/{id}")
    public @ResponseBody ResponseEntity<LivroResponseDTO> put(@PathVariable Integer id, @RequestBody LivroRequestDTO livro){
        LivroResponseDTO salvo = livroService.put(id, livro);
        return ResponseEntity.ok().body(salvo);
    }

    @PatchMapping("/{id}")
    public @ResponseBody ResponseEntity<LivroResponseDTO> patch(@PathVariable Integer id, @RequestBody Map<String, Object> updateMap){
        LivroResponseDTO salvo = livroService.patch(id, updateMap);
        return ResponseEntity.ok().body(salvo);
    }

    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<Void> delete(@PathVariable Integer id){
        livroService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
