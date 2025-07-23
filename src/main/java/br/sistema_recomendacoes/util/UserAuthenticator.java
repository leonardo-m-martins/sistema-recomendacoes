package br.sistema_recomendacoes.util;

import br.sistema_recomendacoes.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserAuthenticator {
    public static boolean authenticate(Usuario usuario){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().contains("ADMIN")) return true;
        }

        return authentication.getName().equals(usuario.getNome());
    }
}
