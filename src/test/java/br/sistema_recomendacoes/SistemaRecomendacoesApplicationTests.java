package br.sistema_recomendacoes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SistemaRecomendacoesApplicationTests {

	@Test
	void contextLoads() {
	}

}
