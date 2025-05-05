package br.sistema_recomendacoes.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sistema_recomendacoes.model.Genero;

public interface GeneroRepository extends JpaRepository<Genero, Long> {
    @Query("SELECT COUNT(l) FROM Livro l JOIN l.generos g WHERE g.id = :genero_id")
    Integer countNum_livros(@Param("genero_id") Integer genero_id);    

    Optional<Genero> findByNome(String nome);

    @Query("SELECT g.nome FROM Genero g")
    Set<String> findAllNome();
}
