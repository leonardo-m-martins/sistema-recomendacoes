package br.sistema_recomendacoes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sistema_recomendacoes.model.Avaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    public Iterable<Avaliacao> findByLivro_id(Integer livro_id);

    @Query(nativeQuery = true, value = "SELECT AVG(nota) FROM avaliacao WHERE livro_id= :livro_id;")
    public Double calcAverage(@Param("livro_id") Integer livro_id);

    @Query(nativeQuery = true, value = "SELECT * FROM avaliacao WHERE livro_id= :livro_id AND usuario_id= :usuario_id;")
    public Optional<Avaliacao> findByLivroAndUsuario(@Param("livro_id") Integer livro_id, @Param("usuario_id") Integer usuario_id);
}
