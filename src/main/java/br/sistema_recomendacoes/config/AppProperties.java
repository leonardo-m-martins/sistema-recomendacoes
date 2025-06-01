package br.sistema_recomendacoes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Limits limits;
    private Weights weights;

    @Data
    public static class Limits {
        private int maxPaginas;
        private int minAno;
        private int maxAno;
        private int notaDeCorte;
        private int notaMax;
        private int notaMin;
    }

    // Pesos
    @Data
    public static class Weights {
        private float generos;
        private float autores;
        private float paginas;
        private float ano;
    }
}
