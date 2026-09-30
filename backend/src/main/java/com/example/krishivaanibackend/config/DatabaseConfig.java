package com.example.krishivaanibackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration for KrishiVaani AI backend.
 *
 * <p>Datasource connection properties are managed via {@code application.properties}
 * (or environment variables DB_URL, DB_USERNAME, DB_PASSWORD for production).
 *
 * <p>Hibernate DDL auto is set to {@code update} so the schema is created/migrated
 * automatically on startup. Switch to {@code validate} for production once schema
 * is stable, and use a migration tool (e.g. Flyway/Liquibase) for schema versioning.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.krishivaanibackend.repository")
public class DatabaseConfig {
    // Datasource bean is auto-configured by Spring Boot via application.properties.
    // Add explicit DataSource / EntityManagerFactory beans here only if custom
    // configuration (e.g. multi-tenancy, multiple datasources) is required.
}
