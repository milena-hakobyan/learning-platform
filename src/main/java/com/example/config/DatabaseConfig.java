package com.example.config;

import com.example.utils.DatabaseConnection;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariDataSource hikariDataSource() {
        return new HikariDataSource();
    }

    @Bean
    public DatabaseConnection dbConnection(HikariDataSource dataSource) {
        return new DatabaseConnection(dataSource);
    }
}
