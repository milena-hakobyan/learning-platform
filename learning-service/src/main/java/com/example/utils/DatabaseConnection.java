package com.example.utils;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DatabaseConnection {
    private final HikariDataSource dataSource;
    public DatabaseConnection(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(String query, Object... args) {//args are the arguments to pass to the prepared statement
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 1; i <= args.length; i++) {
                stmt.setObject(i, args[i - 1]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
        }
    }

    public void execute(String query, Consumer<PreparedStatement> consumer) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            consumer.accept(stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
        }
    }

    public <T> T findOne(String query, Function<ResultSet, T> mapper, Object... args) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 1; i <= args.length; i++) {
                stmt.setObject(i, args[i - 1]);
            }

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                T result = mapper.apply(resultSet);
                if (resultSet.next()) {
                    throw new IllegalStateException("Expected one result, but got more than one");
                }
                return result;
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
        }
    }

    public <T> List<T> findMany(String query, Function<ResultSet, T> mapper, Object... args) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 1; i <= args.length; i++) {
                stmt.setObject(i, args[i - 1]);
            }

            ResultSet resultSet = stmt.executeQuery();

            List<T> resultList = new ArrayList<>();
            while (resultSet.next()) {
                T result = mapper.apply(resultSet);
                resultList.add(result);
            }
            return resultList;
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
        }
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

}