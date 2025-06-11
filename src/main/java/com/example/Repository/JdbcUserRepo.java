package com.example.Repository;

import com.example.Model.Role;
import com.example.Model.User;
import com.example.Utils.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcUserRepo implements UserRepository {
    private final DatabaseConnection dbConnection;

    public JdbcUserRepo(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public User save(User user) {
        String query = "INSERT INTO users (user_name, first_name, last_name, email, user_role, password_hash, last_login) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING *;";

        return dbConnection.findOne(query, this::mapUser, user.getUserName(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getRole().toString(), user.getPassword(), user.getLastLogin());
    }


    @Override
    public void update(User user) {
        String query = "UPDATE users SET " +
                "user_name = ?, " +
                "first_name = ?, " +
                "last_name = ?, " +
                "email = ?, " +
                "user_role = ?, " +
                "password_hash = ?, " +
                "last_login = ?, " +
                "is_active = ? " +
                "WHERE user_id = ?;";

        dbConnection.execute(query,
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().toString(),
                user.getPassword(),
                user.getLastLogin(),
                user.isActive(),
                user.getUserId()
        );
    }

    @Override
    public void delete(Integer userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        dbConnection.execute(query, userId);
    }

    @Override
    public void deactivateUser(Integer userId) {
        String query = "UPDATE users SET is_active = FALSE WHERE user_id = ?";

        dbConnection.execute(query, userId);
    }

    @Override
    public Optional<User> findById(Integer userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";

        return Optional.ofNullable(dbConnection.findOne(query, this::mapUser, userId));
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM users;";

        return dbConnection.findMany(query, this::mapUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?;";

        return Optional.ofNullable(dbConnection.findOne(query, this::mapUser, email));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String query = "SELECT * FROM users WHERE user_name = ?;";

        return Optional.ofNullable(dbConnection.findOne(query, this::mapUser, username));
    }

    @Override
    public List<User> findByRole(Role role) {
        String query = "SELECT * FROM users WHERE user_role = ?;";

        return dbConnection.findMany(query, this::mapUser, role);
    }

    private User mapUser(ResultSet rs) {
        try {
            return new User(
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    Role.valueOf(rs.getString("user_role")),
                    rs.getTimestamp("last_login").toLocalDateTime(),
                    rs.getBoolean("is_active")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to User", e);
        }
    }
}
