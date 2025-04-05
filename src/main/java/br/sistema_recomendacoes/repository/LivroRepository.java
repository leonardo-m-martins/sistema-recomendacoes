package br.sistema_recomendacoes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sistema_recomendacoes.model.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {

}
