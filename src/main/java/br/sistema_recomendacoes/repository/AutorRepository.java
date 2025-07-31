package br.sistema_recomendacoes.repository;

import java.util.Optional;

import br.sistema_recomendacoes.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.sistema_recomendacoes.model.Autor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNome(String nome);

    @Query("SELECT l FROM Livro l JOIN l.autores a WHERE a.id = :autorId")
    Page<Livro> getLivrosByAutor(@Param("autorId") int autorId, Pageable pageable);
}
