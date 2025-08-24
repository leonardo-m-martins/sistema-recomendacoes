package br.sistema_recomendacoes;

import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.service.LivroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SistemaRecomendacoesApplicationTests extends TestcontainersBase {

    @Autowired
    private LivroService livroService;

	@Test
	void testGetAllLivros() {
        Page<LivroResponseDTO> responseDTOS = livroService.findAll(0, 20, "id", "asc");

        // 1️⃣ Verifica se a página não é nula
        assertNotNull(responseDTOS);

        // 2️⃣ Verifica se a lista de conteúdo não é nula
        assertNotNull(responseDTOS.getContent());

        // 3️⃣ Verifica se a página tem até 20 itens (pode ter menos se banco tiver menos)
        assertTrue(responseDTOS.getContent().size() <= 20);

        // 4️⃣ (Opcional) verifica se está ordenado por id ascendente
        List<LivroResponseDTO> livros = responseDTOS.getContent();
        for (int i = 1; i < livros.size(); i++) {
            assertTrue(livros.get(i-1).getId() <= livros.get(i).getId(),
                    "Lista não está ordenada por ID asc");
        }

	}

}
