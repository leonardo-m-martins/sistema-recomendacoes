package br.sistema_recomendacoes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sistema_recomendacoes.model.VetorUsuario;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface VetorUsuarioRepository extends JpaRepository<VetorUsuario, Long> {

    @Query("SELECT v FROM VetorUsuario v")
    Set<VetorUsuario> findAllSet();
}
