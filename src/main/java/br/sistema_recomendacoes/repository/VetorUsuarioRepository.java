package br.sistema_recomendacoes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.sistema_recomendacoes.model.VetorUsuario;

public interface VetorUsuarioRepository extends JpaRepository<VetorUsuario, Long> {
    @Query("SELECT v FROM VetorLivro v")
    VetorUsuario[] findAllArray();
}
