package com.example;


import com.example.utils.DatabaseConnection;
import com.example.utils.DatabaseException;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.concurrent.CountDownLatch;

public class TransactionIsolationDemo {

    public static void main(String[] args) throws InterruptedException {
        HikariDataSource ds = new HikariDataSource();

        ds.setJdbcUrl("jdbc:h2:mem:testdb;LOCK_MODE=1;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTablesQuery = Files.readString(Paths.get("queries/schema-h2.sql"));
            String fillTablesQuery = Files.readString(Paths.get("queries/h2-fill-tables.sql"));

            stmt.executeUpdate(createTablesQuery);
            stmt.executeUpdate(fillTablesQuery);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        DatabaseConnection dbConnection = new DatabaseConnection(ds);

        System.out.println("\n--------------Task #1: Data Consistency Demo--------------\n\n\n");
        demoWithoutAndWithTransactions(dbConnection);

        Thread.sleep(50);
        System.out.println("\n--------------Task #2: Lost Update Problem Demo--------------\n");
        demoLostUpdateIsolationLevels(dbConnection, Connection.TRANSACTION_READ_COMMITTED);
        System.out.println("\n---Change isolation level to REPEATABLE_READ (4) to see effect on lost update---\n\n\n");

    }

    /**
     * Demonstrates inconsistent state without transactions and consistent state with transactions
     */
    public static void demoWithoutAndWithTransactions(DatabaseConnection dbConnection) {
        try {
            System.out.println("Starting simulation WITHOUT transaction:");
            runSimWithoutTransaction(dbConnection);
            System.out.println("Finished simulation WITHOUT transaction.\n");
        } catch (DatabaseException e) {
            System.err.println("Error during simulation without transaction: " + e.getMessage());
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("----------------------------------------------------");

        try {
            System.out.println("Starting simulation WITH transaction:");
            runSimWithTransaction(dbConnection);
            System.out.println("Finished simulation WITH transaction.");
        } catch (RuntimeException e) {
            System.err.println("Error during simulation with transaction: " + e.getMessage());
        }
    }

    /**
     * Runs insertions without transaction — leads to inconsistent DB state on failure
     */
    private static void runSimWithoutTransaction(DatabaseConnection dbConnection) {
        dbConnection.execute("INSERT INTO enrollments (user_id, course_id) VALUES (?, ?)", 1, 5);
        System.out.println("Enrollment inserted");

        // This will fail due to NOT NULL constraint causing inconsistent state
        dbConnection.execute("INSERT INTO activity_logs (user_id, action) VALUES (?, ?)", 1, null);
        System.out.println("Activity log inserted");
    }

    /**
     * Runs insertions inside a transaction — rolls back on failure ensuring consistency
     */
    private static void runSimWithTransaction(DatabaseConnection dbConnection) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false); // start transaction

            try (
                    PreparedStatement enrollStmt = conn.prepareStatement("INSERT INTO enrollments (user_id, course_id) VALUES (?, ?)");
                    PreparedStatement logStmt = conn.prepareStatement("INSERT INTO activity_logs (user_id, action) VALUES (?, ?)")
            ) {
                enrollStmt.setInt(1, 1);
                enrollStmt.setInt(2, 4);
                enrollStmt.executeUpdate();
                System.out.println("Enrollment inserted");

                logStmt.setInt(1, 1);
                logStmt.setString(2, null); // will cause failure due to NOT NULL constraint
                logStmt.executeUpdate();
                System.out.println("Activity log inserted");

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction failed, rolled back");
                throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Transaction error: " + e.getMessage(), e);
        }
    }

    /**
     * Demonstrates lost update problem using default isolation and how it is resolved with higher isolation levels
     * @param dbConnection database connection wrapper
     * @param isolationLevel JDBC isolation level to use (e.g., TRANSACTION_READ_COMMITTED, TRANSACTION_REPEATABLE_READ)
     */
    public static void demoLostUpdateIsolationLevels(DatabaseConnection dbConnection, int isolationLevel) throws InterruptedException {
        int userId = 10;
        CountDownLatch startLatch = new CountDownLatch(1);

        Runnable transactionA = () -> {
            try (Connection conn = dbConnection.getConnection()) {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(isolationLevel);

                startLatch.await();

                PreparedStatement readStmt = conn.prepareStatement("SELECT completed_courses FROM students WHERE user_id = ?");
                readStmt.setInt(1, userId);
                ResultSet rs = readStmt.executeQuery();
                rs.next();
                int completed = rs.getInt("completed_courses");
                System.out.println("Transaction A reads completed_courses = " + completed);

                int newVal = completed + 5;
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE students SET completed_courses = ? WHERE user_id = ?");
                updateStmt.setInt(1, newVal);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();

                conn.commit();
                System.out.println("Transaction A commits completed_courses = " + newVal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable transactionB = () -> {
            try (Connection conn = dbConnection.getConnection()) {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

                startLatch.await();

                PreparedStatement readStmt = conn.prepareStatement("SELECT completed_courses FROM students WHERE user_id = ?");
                readStmt.setInt(1, userId);
                ResultSet rs = readStmt.executeQuery();
                rs.next();
                int completed = rs.getInt("completed_courses");
                System.out.println("Transaction B reads completed_courses = " + completed);

                int newVal = completed + 1;
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE students SET completed_courses = ? WHERE user_id = ?");
                updateStmt.setInt(1, newVal);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();

                conn.commit();
                System.out.println("Transaction B commits completed_courses = " + newVal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread tA = new Thread(transactionA);
        Thread tB = new Thread(transactionB);

        tA.start();
        tB.start();

        startLatch.countDown();

        tA.join();
        tB.join();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement finalStmt = conn.prepareStatement("SELECT completed_courses FROM students WHERE user_id = ?")) {
            finalStmt.setInt(1, userId);
            ResultSet rs = finalStmt.executeQuery();
            if (rs.next()) {
                int finalValue = rs.getInt("completed_courses");
                System.out.println("\nFinal completed_courses = " + finalValue + " (Expected: 6, Lost update)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

