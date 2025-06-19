package com.example;

import com.example.utils.DatabaseConnection;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {
    private static HikariDataSource dataSource;
    private DatabaseConnection databaseConnection;

    @BeforeAll
    static void initDatabase() throws Exception {
        HikariDataSource ds = new HikariDataSource();
        // this means connecting to h2 db through jdbc, and calling the db "testdb", and the close-delay means that when the jvm stops the db will be deleted since it resides in RAM
        ds.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa"); //default user for h2
        ds.setPassword(""); //no password by-default
        dataSource = ds;

        // now we have to fill the in-memory fake/test db with our data, so we have to run the schema-h2.sql and the h2-fill-tables.sql
        // since the syntax of postgres and h2 are a bit different, I created 2 new scripts tailored for h2 logic (just a line or two is different, everything else is the same)
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTablesQuery = Files.readString(Paths.get("queries/schema-h2.sql"));
            String fillTablesQuery = Files.readString(Paths.get("queries/h2-fill-tables.sql"));

            stmt.executeUpdate(createTablesQuery);
            stmt.executeUpdate(fillTablesQuery);
        }
    }

    @BeforeEach
    void setUp() {
        databaseConnection = new DatabaseConnection(dataSource);
    }


    @Test
    public void testExecuteArgsInsert() {
        String insertQuery = "INSERT INTO users (user_name, first_name, last_name, email, user_role, password_hash, last_login) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        databaseConnection.execute(insertQuery,
                "jdoe",                    // user_name
                "John",                    // first_name
                "Doe",                     // last_name
                "jdoe@example.com",        // email
                "student",                 // user_role
                "hashedpassword123",       // password_hash
                Timestamp.valueOf("2025-06-02 12:00:00") // last_login
        );

        String selectQuery = "SELECT user_name, first_name, last_name FROM users WHERE user_name = ?";

        var result = databaseConnection.findOne(selectQuery, rs -> {
            try {
                return rs.getString("user_name") + " " + rs.getString("first_name") + " " + rs.getString("last_name");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, "jdoe");

        assertNotNull(result);
        assertEquals("jdoe John Doe", result);
    }

    @Test
    public void testExecuteArgsUpdate() {
        databaseConnection.execute(
                "INSERT INTO users (user_name, first_name, last_name, email, user_role, password_hash, last_login) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "abdulbari", "Abdul", "Bari", "abdul@example.com", "student", "hashed123", Timestamp.valueOf("2025-06-02 12:00:00")
        );

        databaseConnection.execute(
                "UPDATE users SET email = ?, user_role = ? WHERE user_name = ?",
                "abdul.bari@updated.com", "instructor", "abdulbari"
        );

        String selectQuery = "SELECT email, user_role FROM users WHERE user_name = ?";
        String result = databaseConnection.findOne(selectQuery, rs -> {
            try {
                return rs.getString("email") + " - " + rs.getString("user_role");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, "abdulbari");

        assertNotNull(result);
        assertEquals("abdul.bari@updated.com - INSTRUCTOR", result);
    }


    @Test
    public void testExecuteArgsDeleteUser() {
        databaseConnection.execute(
                "INSERT INTO users (user_name, first_name, last_name, email, user_role, password_hash, last_login) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "tempuser", "Temp", "User", "temp@example.com", "student", "tempPass", Timestamp.valueOf("2025-06-02 13:00:00")
        );

        databaseConnection.execute(
                "DELETE FROM users WHERE user_name = ?",
                "tempuser"
        );

        int result = databaseConnection.findOne(
                "SELECT COUNT(*) FROM users WHERE user_name = ?",
                rs -> {
                    try {
                        return rs.getInt(1);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                "tempuser"
        );

        assertEquals(0, result);
    }


    @Test
    public void testExecuteConsumerInsert() {
        String insertQuery = "INSERT INTO users (user_name, first_name, last_name, email, user_role, password_hash, last_login) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        databaseConnection.execute(insertQuery, stmt -> {
            try {
                stmt.setString(1, "asmith");
                stmt.setString(2, "Alice");
                stmt.setString(3, "Smith");
                stmt.setString(4, "asmith@example.com");
                stmt.setString(5, "instructor");
                stmt.setString(6, "anotherhashedpass");
                stmt.setTimestamp(7, Timestamp.valueOf("2025-06-02 15:30:00"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        String selectQuery = "SELECT user_name, first_name, last_name FROM users WHERE user_name = ?";

        String result = databaseConnection.findOne(selectQuery, rs -> {
            try {
                return rs.getString("user_name") + " " + rs.getString("first_name") + " " + rs.getString("last_name");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, "asmith");

        assertNotNull(result);
        assertEquals("asmith Alice Smith", result);
    }

    @Test
    public void testExecuteFailsWithNullConstraintViolation() {
        assertThrows(RuntimeException.class, () -> {
            databaseConnection.execute(
                    "INSERT INTO users (user_name, first_name, last_name, email, user_role, password_hash, last_login) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    null, null, null, null, null, null, null // violates NOT NULL constraints
            );
        });
    }


    @Test
    void testInsertDuplicateEmailThrows() {
        databaseConnection.execute(
                "INSERT INTO users (first_name, last_name, email, user_role, password_hash, last_login) VALUES (?, ?, ?, ?, ?, ?)",
                "John", "Doe", "johnn@example.com", "STUDENT", "hash", null);

        assertThrows(RuntimeException.class, () -> {
            databaseConnection.execute(
                    "INSERT INTO users (first_name, last_name, email, user_role, password_hash, last_login) VALUES (?, ?, ?, ?, ?, ?)",
                    "Jane", "Smith", "johnn@example.com", "STUDENT", "hash", null);
        });
    }

    @Test
    void testExecuteWithParameterCountMismatchThrows() {
        assertThrows(RuntimeException.class, () -> {
            databaseConnection.execute(
                    "INSERT INTO users (first_name, last_name, email) VALUES (?, ?, ?)",
                    "John", "Doe"); // Missing 1 parameter
        });

        assertThrows(RuntimeException.class, () -> {
            databaseConnection.execute(
                    "INSERT INTO users (first_name, last_name) VALUES (?, ?)",
                    "John", "Doe", "extraParam"); // Extra parameter
        });
    }


    @Test
    public void testFindOneReturnsCorrectResult() {
        databaseConnection.execute(
                "INSERT INTO users (user_name, first_name, last_name, email, user_role, password_hash, last_login) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "student11", "Jennifer", "Doe", "janedoe@example.com", "student", "secureHash", Timestamp.valueOf("2025-06-02 08:30:00")
        );

        String retrievedEmail = databaseConnection.findOne(
                "SELECT email FROM users WHERE user_name = ?",
                rs -> {
                    try {
                        return rs.getString("email");
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to read email from ResultSet", e);
                    }
                },
                "student11"
        );

        assertNotNull(retrievedEmail);
        assertEquals("janedoe@example.com", retrievedEmail);
    }

    @Test
    public void testFindOneWhenMultipleResults() {
        assertThrows(IllegalStateException.class, () -> {
            databaseConnection.findOne(
                    "SELECT user_name FROM users WHERE user_role = ?",
                    rs -> {
                        try {
                            return rs.getString("user_name");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    "student"
            );
        });
    }

    @Test
    public void testFindManyReturnsAllResults() {
        List<String> emails = databaseConnection.findMany(
                "SELECT email FROM users WHERE user_role = ?",
                rs -> {
                    try {
                        return rs.getString("email");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                "admin"
        );

        assertEquals(5, emails.size());
    }

    @Test
    public void testFindManyReturnsEmptyListWhenNoResults() {
        List<String> results = databaseConnection.findMany(
                "SELECT * FROM users WHERE user_name = ?",
                rs -> {
                    try {
                        return rs.getString("user_name");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                "nonexistentuserName"
        );

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

}