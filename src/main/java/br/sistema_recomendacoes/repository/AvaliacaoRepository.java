package br.sistema_recomendacoes.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sistema_recomendacoes.model.Avaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Iterable<Avaliacao> findByLivro_id(Integer livro_id);

    Iterable<Avaliacao> findByUsuario_id(Integer usuario_id);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.livro.id = :livro_id")
    Double calcAverage(@Param("livro_id") Integer livro_id);

    @Query("SELECT a FROM Avaliacao a WHERE a.livro.id = :livro_id AND a.usuario.id = :usuario_id")
    Optional<Avaliacao> findByLivroAndUsuario(@Param("livro_id") Integer livro_id, @Param("usuario_id") Integer usuario_id);

    @Query("SELECT a.livro.id FROM Avaliacao a WHERE a.usuario.id = :usuario_id")
    Set<Integer> findLivro_idByUsuario_id(@Param("usuario_id") Integer usuario_id);
}
