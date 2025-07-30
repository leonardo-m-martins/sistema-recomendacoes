package br.sistema_recomendacoes.repository;

import br.sistema_recomendacoes.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sistema_recomendacoes.model.Genero;

public interface GeneroRepository extends JpaRepository<Genero, Long> {
    @Query("SELECT COUNT(l) FROM Livro l JOIN l.generos g WHERE g.id = :genero_id")
    Integer countNum_livros(@Param("genero_id") Integer genero_id);

    @Query("SELECT l FROM Livro l JOIN l.generos g WHERE g.id = :generoId")
    Page<Livro> findAllLivrosByGenero(@Param("generoId") int generoId, Pageable pageable);
}
