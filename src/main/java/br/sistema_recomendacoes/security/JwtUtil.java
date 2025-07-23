package br.sistema_recomendacoes.security;

import br.sistema_recomendacoes.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "GPCRSQH14DMZG244GIID4LTTPDZ2MCI5131J5YQC";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        // Converte a chave para um formato binário seguro
        this.secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String username, Role role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
