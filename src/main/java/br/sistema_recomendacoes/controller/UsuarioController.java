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

import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.dto.UsuarioRequestDTO;
import br.sistema_recomendacoes.dto.UsuarioResponseDTO;
import br.sistema_recomendacoes.service.AuthService;
import br.sistema_recomendacoes.service.UsuarioService;

@RestController
@RequestMapping(path = "/api/")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthService authService;

    // POST: Novo usuario
    @PostMapping(path = "/auth/cadastrar")
    public @ResponseBody ResponseEntity<UsuarioResponseDTO> add(@RequestBody UsuarioRequestDTO usuario) {
        UsuarioResponseDTO salvo = authService.add(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping(path = "/auth/login")
    public @ResponseBody ResponseEntity<Map<String, Object>> login(@RequestBody UsuarioRequestDTO requestDTO){
        Map<String, Object> response = authService.login(requestDTO);
        return ResponseEntity.ok().body(response);
    }

    // GET: todos os usuarios
    @GetMapping(path = "/usuario/")
    public @ResponseBody ResponseEntity<List<UsuarioResponseDTO>> getAll() {
        List<UsuarioResponseDTO> responseDTOs = usuarioService.getAllUsuarios();
        return ResponseEntity.ok().body(responseDTOs);
    }

    // GET: por ID
    @GetMapping(path = "/usuario/{id}")
    public @ResponseBody ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Integer id){
        UsuarioResponseDTO usuario = usuarioService.findByIdDto(id);
        return ResponseEntity.status(HttpStatus.OK).body(usuario);
    }

    // PUT: update todos os campos
    @PutMapping("/usuario/{id}")
    public @ResponseBody ResponseEntity<UsuarioResponseDTO> put(@PathVariable Integer id, @RequestBody UsuarioRequestDTO usuario) {
        UsuarioResponseDTO salvo = authService.put(id, usuario);
        return ResponseEntity.status(HttpStatus.OK).body(salvo);
    }

    // PATCH: update campos opcionais
    @PatchMapping("/usuario/{id}")
    public @ResponseBody ResponseEntity<UsuarioResponseDTO> patch(@PathVariable Integer id, @RequestBody Map<String, Object> updateMap) {
        UsuarioResponseDTO salvo = authService.patch(id, updateMap);
        return ResponseEntity.status(HttpStatus.OK).body(salvo);
    }

    // DELETE: deletar
    @DeleteMapping("/usuario/{id}")
    public @ResponseBody ResponseEntity<Void> delete(@PathVariable Integer id){
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // histórico
    @GetMapping("/usuario/{id}/historico")
    public @ResponseBody ResponseEntity<List<LivroResponseDTO>> historico(@PathVariable Integer id){
        List<LivroResponseDTO> dtos = usuarioService.historico(id);
        return ResponseEntity.ok().body(dtos);
    }
}
