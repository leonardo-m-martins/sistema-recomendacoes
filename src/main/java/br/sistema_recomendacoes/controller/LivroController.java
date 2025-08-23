package br.sistema_recomendacoes.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.sistema_recomendacoes.dto.LivroRequestDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.service.LivroService;
import br.sistema_recomendacoes.util.Cronometro;

@RestController
@RequestMapping(path = "/livro")
public class LivroController {
    
    @Autowired
    private LivroService livroService;

    @PostMapping("/")
    public @ResponseBody ResponseEntity<LivroResponseDTO> add(@RequestBody LivroRequestDTO livro){
        LivroResponseDTO salvo = livroService.add(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/")
    public @ResponseBody ResponseEntity<Page<LivroResponseDTO>> findAll(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction){
        Page<LivroResponseDTO> livros = livroService.findAll(page, size, sortBy, direction);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(14, TimeUnit.DAYS).cachePublic())
                .body(livros);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<LivroResponseDTO> findById(@PathVariable Integer id){
        LivroResponseDTO livro = livroService.findByIdDto(id);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(livro);
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

    @PostMapping("/list")
    public @ResponseBody ResponseEntity<Map<String, String>> addMany(@RequestBody List<LivroRequestDTO> requestDTOs){

        Cronometro cronometro = new Cronometro();

        cronometro.start();
        Integer livrosSalvos = livroService.addMany(requestDTOs);
        double duracao = cronometro.stop();

        Map<String, String> resposta = new HashMap<>();
        resposta.put("total_salvos", String.valueOf(livrosSalvos));
        resposta.put("time", String.valueOf(duracao));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/search")
    public @ResponseBody ResponseEntity<Page<LivroResponseDTO>> search(@RequestParam(name = "q") String q, @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sortBy,
                                                                       @RequestParam(defaultValue = "asc") String direction){
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(livroService.search(q, page, size, sortBy, direction));
    }
}
