package br.sistema_recomendacoes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sistema_recomendacoes.model.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    @Query(
            value = """
            SELECT l.*
            FROM livro l
            JOIN _livro_busca lb ON lb.livro_id = l.id
            WHERE MATCH(lb.titulo, lb.descricao) AGAINST(:termo IN NATURAL LANGUAGE MODE)
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM livro l
            JOIN _livro_busca lb ON lb.livro_id = l.id
            WHERE MATCH(lb.titulo, lb.descricao) AGAINST(:termo IN NATURAL LANGUAGE MODE)
            """,
            nativeQuery = true
    )
    Page<Livro> buscaPorTituloDescricao(@Param("termo") String termo, Pageable pageable);
}

