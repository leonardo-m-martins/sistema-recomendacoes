package br.sistema_recomendacoes.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        CaffeineCache recomendacaoConteudo = new CaffeineCache(
                "recomendacaoConteudo",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.DAYS)
                        .maximumSize(1000)
                        .build()
        );

        CaffeineCache recomendacaoColaborativa = new CaffeineCache(
                "recomendacaoColaborativa",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(1000)
                        .build()
        );

        cacheManager.setCaches(Arrays.asList(recomendacaoConteudo, recomendacaoColaborativa));
        return cacheManager;
    }
}
