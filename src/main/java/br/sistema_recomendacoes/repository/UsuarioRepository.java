package br.sistema_recomendacoes.repository;

import br.sistema_recomendacoes.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.sistema_recomendacoes.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByNome(String nome);
    Usuario findByEmail(String email);

    @Query("""
SELECT l
FROM Livro l
JOIN l.avaliacaos a
WHERE a.usuario.id = :usuarioId
""")
    Page<Livro> getHistorico(@Param("usuarioId") int usuarioId, Pageable pageable);
}
