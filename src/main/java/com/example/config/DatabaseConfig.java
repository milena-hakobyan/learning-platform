package com.example.config;

import com.example.utils.DatabaseConnection;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
    @Bean
    public DatabaseConnection dbConnection(HikariDataSource dataSource) {
        return new DatabaseConnection(dataSource);
    }
}