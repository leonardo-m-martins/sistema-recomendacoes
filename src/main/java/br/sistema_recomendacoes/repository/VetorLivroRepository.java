package br.sistema_recomendacoes.repository;

import br.sistema_recomendacoes.model.VetorLivro;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VetorLivroRepository extends JpaRepository<VetorLivro, Long> {

    @Query("SELECT v FROM VetorLivro v")
    Set<VetorLivro> findAllSet();
}
