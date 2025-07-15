package com.example.tasks;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class SqlInjectionDemo {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

        System.out.println("SQL Injection test with Statement\n");
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            /*
                 By appending `OR 1=1` to the query, Alice can bypass authentication checks.
                Since `1=1` is always true, the entire WHERE clause becomes true,
                allowing her to retrieve all records from the users table — even without valid credentials.
             */
            String query = "SELECT * FROM users WHERE email = 'alice@example.com' AND password_hash = 'hash1' OR 1=1";
            ResultSet resultSet = stmt.executeQuery(query);
            while(resultSet.next()){
                String userName = resultSet.getString("user_name");
                String email = resultSet.getString("email");
                String pass = resultSet.getString("password_hash");
                System.out.println(userName + "     " + email + "     " + pass);
            }

        } catch (SQLException e) {
            System.err.println("Connection failed:");
            e.printStackTrace();
        }
        System.out.println();


        /*
            This input tries to perform an SQL injection by including "OR 1=1" in the password field.
            In a normal Statement, this tricked the SQL engine into returning all rows.
            However, PreparedStatement treats each input as a literal value, not as part of the SQL syntax.
            So the string "hash1 OR 1=1" is searched as an exact password, which fails unless such a password exists.
            This shows how PreparedStatement protects against SQL injection.
         */

        System.out.println("SQL Injection test with PreparedStatement\n");
        String safeQuery = "SELECT * FROM users WHERE email = ? AND password_hash = ?";
        try(Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(safeQuery)) {
            stmt.setString(1, "alice@example.com");
            stmt.setString(2, "hash1 OR 1=1");
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()){
                String userName = resultSet.getString("user_name");
                String email = resultSet.getString("email");
                String pass = resultSet.getString("password_hash");
                System.out.println(userName + "     " + email + "     " + pass);
            }

        } catch (SQLException e) {
            System.err.println("Connection failed:");
            e.printStackTrace();
        }

    }
}