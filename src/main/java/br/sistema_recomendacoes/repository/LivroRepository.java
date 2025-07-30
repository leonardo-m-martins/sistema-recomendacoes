package br.sistema_recomendacoes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sistema_recomendacoes.model.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    // @Query(value = """
    //     SELECT DISTINCT l.*
    //     FROM livro l
    //     JOIN livro_autor la ON l.id = la.livro_id
    //     JOIN autor a ON la.autor_id = a.id
    //     WHERE MATCH(l.titulo, l.descricao) AGAINST(:termo IN NATURAL LANGUAGE MODE)
    //        OR a.nome LIKE CONCAT('%', :termo, '%')
    //     """, nativeQuery = true)
    @Query(value = """
        SELECT l.*
        FROM livro l
        JOIN _livro_busca lb ON lb.livro_id = l.id
        WHERE MATCH(lb.titulo, lb.descricao) AGAINST(:termo IN NATURAL LANGUAGE MODE)
        """, nativeQuery = true)
    List<Livro> buscaPorTextoEAutor(@Param("termo") String termo);
}
