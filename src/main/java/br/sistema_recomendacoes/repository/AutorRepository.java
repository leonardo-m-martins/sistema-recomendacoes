package br.sistema_recomendacoes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sistema_recomendacoes.model.Autor;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNome(String nome);
}
