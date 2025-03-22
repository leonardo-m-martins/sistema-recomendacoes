package br.sistema_recomendacoes;

import org.springframework.boot.SpringApplication;

public class TestSistemaRecomendacoesApplication {

	public static void main(String[] args) {
		SpringApplication.from(SistemaRecomendacoesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
