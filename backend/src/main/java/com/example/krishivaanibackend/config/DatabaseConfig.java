package com.example.krishivaanibackend.config;

import com.example.krishivaanibackend.service.EmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration for KrishiVaani AI backend.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.krishivaanibackend.repository")
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    public CommandLineRunner initAgriculturalVectors(EmbeddingService embeddingService) {
        return args -> {
            try {
                log.info("Running automatic agricultural document & vector initialization...");
                int count = embeddingService.embedAll();
                log.info("Agricultural vector initialization complete: {} documents ready in PgVector.", count);
            } catch (Exception e) {
                log.error("Failed to initialize agricultural vectors on startup: {}", e.getMessage(), e);
            }
        };
    }
}
