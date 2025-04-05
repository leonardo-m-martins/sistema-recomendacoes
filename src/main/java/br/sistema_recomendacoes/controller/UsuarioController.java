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

import br.sistema_recomendacoes.dto.UsuarioRequestDTO;
import br.sistema_recomendacoes.dto.UsuarioResponseDTO;
import br.sistema_recomendacoes.service.UsuarioService;

@RestController
@RequestMapping(path = "/api/usuario")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    // POST: Novo usuario
    @PostMapping(path = "/")
    public @ResponseBody ResponseEntity<UsuarioResponseDTO> add(@RequestBody UsuarioRequestDTO usuario) {
        UsuarioResponseDTO salvo = usuarioService.add(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // GET: todos os usuarios
    @GetMapping(path = "/")
    public @ResponseBody ResponseEntity<List<UsuarioResponseDTO>> getAll() {
        List<UsuarioResponseDTO> responseDTOs = usuarioService.getAllUsuarios();
        return ResponseEntity.ok().body(responseDTOs);
    }

    // GET: por ID
    @GetMapping(path = "/{id}")
    public @ResponseBody ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Integer id){
        UsuarioResponseDTO usuario = usuarioService.findByIdDto(id);
        return ResponseEntity.status(HttpStatus.OK).body(usuario);
    }

    // PUT: update todos os campos
    @PutMapping("/{id}")
    public @ResponseBody ResponseEntity<UsuarioResponseDTO> put(@PathVariable Integer id, @RequestBody UsuarioRequestDTO usuario) {
        UsuarioResponseDTO salvo = usuarioService.put(id, usuario);
        return ResponseEntity.status(HttpStatus.OK).body(salvo);
    }

    // PATCH: update campos opcionais
    @PatchMapping("/{id}")
    public @ResponseBody ResponseEntity<UsuarioResponseDTO> patch(@PathVariable Integer id, @RequestBody Map<String, Object> updateMap) {
        UsuarioResponseDTO salvo = usuarioService.patch(id, updateMap);
        return ResponseEntity.status(HttpStatus.OK).body(salvo);
    }

    // DELETE: deletar
    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<Void> delete(@PathVariable Integer id){
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
