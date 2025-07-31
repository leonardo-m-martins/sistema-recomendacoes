package br.sistema_recomendacoes.controller;

import br.sistema_recomendacoes.dto.AutorResponseDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/autor")
public class AutorController {

    @Autowired
    private AutorService autorService;

//    Retorna autor pelo id
    @GetMapping("/{id}")
    public ResponseEntity<AutorResponseDTO> getAutorById(@PathVariable Integer id){
        AutorResponseDTO responseDTO = autorService.findByIdDto(id);

        return ResponseEntity.ok().body(responseDTO);
    }

//    Retorna todos os livros do autor
    @GetMapping("/livros/{id}")
    public ResponseEntity<Page<LivroResponseDTO>> getLivrosByAutorId(@PathVariable Integer id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
                                                                    @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String direction) {
        Page<LivroResponseDTO> responseDTOS = autorService.getLivros(id, page, size, sortBy, direction);

        return ResponseEntity.ok().body(responseDTOS);
    }
}
