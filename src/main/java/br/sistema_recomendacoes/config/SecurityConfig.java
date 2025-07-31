package br.sistema_recomendacoes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import br.sistema_recomendacoes.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <-- ADICIONE ESTA LINHA
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/recomendacao/colaborativa/**").hasAnyRole("GUEST", "USER", "ADMIN")
                    .requestMatchers("/recomendacao/conteudo/**").hasAnyRole("GUEST", "USER", "ADMIN")
                    .requestMatchers("/recomendacao/**").hasRole("ADMIN")
                    .requestMatchers("/avaliacao/**").hasAnyRole("GUEST", "USER", "ADMIN")
                    .requestMatchers("/usuario/*/historico").hasAnyRole("GUEST", "USER", "ADMIN")
                    .requestMatchers("/usuario/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET,"/genero/**").hasAnyRole("GUEST", "USER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/genero/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/genero/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/genero/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/livro/**").hasAnyRole("GUEST", "USER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/livro/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/livro/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/livro/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/autor/**").hasAnyRole("GUEST", "USER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/autor/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/autor/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/autor/**").hasRole("ADMIN")
                    .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // solução temporária para erros de CORS TODO: solução permanente para CORS
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true); // se estiver usando cookies ou auth headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

