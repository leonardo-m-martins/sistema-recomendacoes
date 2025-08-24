package br.sistema_recomendacoes;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@Testcontainers
public class TestcontainersBase {

    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(
            DockerImageName.parse("ghcr.io/leonardo-m-martins/reclivros-mysql:latest")
                .asCompatibleSubstituteFor("mysql:8.0")
        )
            .withStartupTimeout(Duration.ofMinutes(5))
            .withDatabaseName("sistema_recomendacoes")
            .withUsername("root")
            .withPassword("root");

    static {
        mysqlContainer.start();
    }

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }
}
