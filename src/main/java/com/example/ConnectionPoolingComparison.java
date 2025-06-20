package com.example;

import com.example.utils.SingleConnectionDataSource;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

public class ConnectionPoolingComparison {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
        try {
            System.out.println("Running with custom SingleConnectionDataSource...");
            long singleConnectionDuration = runWithCustomDataSource(url, user, password);
            System.out.println("Time taken (SingleConnection): " + singleConnectionDuration + " ms");

            System.out.println("\n========================================\n");

            System.out.println("\nRunning with HikariCP...");
            long hikariDuration = runWithHikariDataSource(url, user, password);
            System.out.println("Time taken (HikariCP): " + hikariDuration + " ms");


        } catch (Exception e) {
            System.err.println("An error occurred during execution: " + e.getMessage());
        }
    }

    private static long runWithCustomDataSource(String url, String user, String password) throws SQLException, InterruptedException {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(url, user, password);
        return simulateWithSingleConnection(dataSource);
    }

    private static long runWithHikariDataSource(String url, String user, String password) throws SQLException, InterruptedException {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(5);
        dataSource.setConnectionTimeout(3000);
        long duration =  simulateWithHikari(dataSource);
        dataSource.close();

        return duration;
    }

    private static long simulateWithSingleConnection(DataSource dataSource) throws InterruptedException {
        int numberOfThreads = 7;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        long start = System.currentTimeMillis();

        for (int i = 0; i < numberOfThreads; i++) {
            Thread t = new Thread(() -> {
                // Synchronize on the datasource to ensure one thread uses the single connection at a time
                synchronized (dataSource) {
                    try {
                        Connection conn = dataSource.getConnection();  // single shared connection
                        try (PreparedStatement stmt = conn.prepareStatement("SELECT pg_sleep(2)")) {
                            stmt.execute();
                        }
                    } catch (SQLException e) {
                        System.err.println("Connection failed: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }
            });
            t.start();
        }
        latch.await();
        return System.currentTimeMillis() - start;
    }

    private static long simulateWithHikari(DataSource dataSource) throws InterruptedException {
        int numberOfThreads = 7;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        long start = System.currentTimeMillis();

        for (int i = 0; i < numberOfThreads; i++) {
            Thread t = new Thread(() -> {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("SELECT pg_sleep(2)")) {
                    stmt.execute();
                } catch (SQLException e) {
                    System.err.println("Connection failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
            t.start();
        }

        latch.await();
        return System.currentTimeMillis() - start;
    }
}