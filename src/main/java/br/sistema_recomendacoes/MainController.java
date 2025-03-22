package br.sistema_recomendacoes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class MainController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping(path = "/add")
    public @ResponseBody String addNewUser (@RequestParam String username,
    @RequestParam String password){
        Usuario n = new Usuario(username, password);
        usuarioRepository.save(n);
        return "Salvo";
    }

    @PostMapping(path = "/remove")
    public @ResponseBody String removeUser (@RequestParam String username){
        Usuario n = usuarioRepository.findByUsername(username);
        usuarioRepository.delete(n);
        return "Deletado";
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Usuario> getAllUsuarios(){
        Iterable<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios;
    }
}
