package br.sistema_recomendacoes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sistema_recomendacoes.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByNome(String nome);
    Usuario findByEmail(String email);
}
