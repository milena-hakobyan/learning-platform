package com.example;

import com.example.Utils.DatabaseConnection;
import com.example.Utils.SingleConnectionDataSource;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

public class ConnectionPoolingComparison {
    public static void main(String[] args) {
        try {
            System.out.println("Running with custom SingleConnectionDataSource...");
            long singleConnectionDuration = runWithCustomDataSource();
            System.out.println("Time taken (SingleConnection): " + singleConnectionDuration + " ms");

            System.out.println("\n========================================\n");

            System.out.println("\nRunning with HikariCP...");
            long hikariDuration = runWithHikariDataSource();
            System.out.println("Time taken (HikariCP): " + hikariDuration + " ms");


        } catch (Exception e) {
            System.err.println("An error occurred during execution: " + e.getMessage());
        }
    }

    private static long runWithCustomDataSource() throws SQLException, InterruptedException {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource("jdbc:postgresql://localhost:5432/learning-db", "admin", "secret");
        return simulateWithSingleConnection(dataSource);
    }

    private static long runWithHikariDataSource() throws SQLException, InterruptedException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/learning-db");
        dataSource.setUsername("admin");
        dataSource.setPassword("secret");

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