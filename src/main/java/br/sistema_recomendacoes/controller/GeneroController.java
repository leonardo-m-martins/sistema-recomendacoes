package br.sistema_recomendacoes.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import br.sistema_recomendacoes.dto.GeneroRequestDTO;
import br.sistema_recomendacoes.dto.GeneroResponseDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.service.GeneroService;

@RestController
@RequestMapping(path = "/genero")
public class GeneroController {
    
    @Autowired
    private GeneroService generoService;

    // create
    @PostMapping("/")
    public @ResponseBody ResponseEntity<GeneroResponseDTO> add(@RequestBody GeneroRequestDTO genero){
        GeneroResponseDTO salvo = generoService.add(genero);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // read all
    @GetMapping("/")
    public @ResponseBody ResponseEntity<Page<GeneroResponseDTO>> findAll(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction){
        return ResponseEntity.ok().body(generoService.findAllDto(page, size, sortBy, direction));
    }

    // read one
    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<GeneroResponseDTO> findById(@PathVariable Integer id){
        GeneroResponseDTO genero = generoService.findByIdDto(id);
        return ResponseEntity.status(HttpStatus.OK).body(genero);
    }

    // put
    @PutMapping("/{id}")
    public @ResponseBody ResponseEntity<GeneroResponseDTO> put(@PathVariable Integer id, @RequestBody GeneroRequestDTO genero){
        GeneroResponseDTO salvo = generoService.put(id, genero);
        return ResponseEntity.status(HttpStatus.OK).body(salvo);
    }

    // patch
    @PatchMapping("/{id}")
    public @ResponseBody ResponseEntity<GeneroResponseDTO> patch(@PathVariable Integer id, @RequestBody Map<String, Object> updateMap){
        GeneroResponseDTO salvo = generoService.patch(id, updateMap);
        return ResponseEntity.status(HttpStatus.OK).body(salvo);
    }

    // delete
    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<Void> delete(@PathVariable Integer id){
        generoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // atualizar o número de livros de cada gênero.
    // é mais eficiente salvar o valor no banco de dados esporadicamente, do que procurar por ele toda vez.
    @PostMapping("/num-livros")
    public @ResponseBody ResponseEntity<Void> updateNum_livros(){
        generoService.updateNum_livros();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/livros/{id}")
    public @ResponseBody ResponseEntity<Page<LivroResponseDTO>> getLivros(@PathVariable Integer id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
                                                                          @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String direction){
        Page<LivroResponseDTO> livroResponseDTOS = generoService.getLivros(id, page, size, sortBy, direction);
        return ResponseEntity.ok().body(livroResponseDTOS);
    }

    
}
