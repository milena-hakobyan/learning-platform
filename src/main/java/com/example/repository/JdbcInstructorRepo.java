package com.example.repository;

import com.example.model.Instructor;
import com.example.model.User;
import com.example.utils.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
public class JdbcInstructorRepo implements InstructorRepository {
    private final DatabaseConnection dbConnection;
    private final UserRepository userRepo;

    public JdbcInstructorRepo(DatabaseConnection dbConnection, JdbcUserRepo userRepo) {
        this.dbConnection = dbConnection;
        this.userRepo = userRepo;
    }

    @Override
    public Instructor save(Instructor instructor) {
        User savedUser = userRepo.save(instructor);
        instructor.setId(savedUser.getId());

        String insertInstructorQuery = "INSERT INTO instructors (user_id, bio, total_courses_created, rating, is_verified) VALUES (?, ?, ?, ?, ?)";
        dbConnection.execute(insertInstructorQuery, savedUser.getId(), instructor.getBio(), instructor.getTotalCoursesCreated(), instructor.getRating(), instructor.isVerified());

        return findById(savedUser.getId()).orElseThrow(() -> new RuntimeException("Failed to retrieve saved instructor"));
    }

    @Override
    public Instructor update(Instructor instructor) {
        String updateQuery = """
                    UPDATE instructors
                    SET bio = ?, total_courses_created = ?, rating = ?, is_verified = ?
                    WHERE user_id = ?
                    RETURNING *;
                """;

        dbConnection.findOne(updateQuery, rs -> {
            // no need to return full Instructor because we return from findById
            return true;
        }, instructor.getBio(), instructor.getTotalCoursesCreated(), instructor.getRating(), instructor.isVerified(), instructor.getId());

        return findById(instructor.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve updated instructor"));
    }


    @Override
    public void delete(Integer userId) {
        String deleteUserQuery = "DELETE FROM users WHERE id = ?";
        dbConnection.execute(deleteUserQuery, userId);
    }

    @Override
    public Optional<Instructor> findById(Integer id) {
        String query = """
                    SELECT u.id, u.user_name, u.first_name, u.last_name, u.email, u.password_hash,
                           u.last_login, u.is_active,
                           i.bio, i.total_courses_created, i.rating, i.is_verified
                    FROM instructors i
                    JOIN users u ON i.user_id = u.id
                    WHERE i.user_id = ?
                """;

        Instructor instructor = dbConnection.findOne(query, this::mapToInstructor, id);
        return Optional.ofNullable(instructor);
    }

    @Override
    public List<Instructor> findAll() {
        String query = """
                    SELECT u.id, u.user_name, u.first_name, u.last_name, u.email, u.password_hash,
                           u.last_login, u.is_active,
                           i.bio, i.total_courses_created, i.rating, i.is_verified
                    FROM instructors i
                    JOIN users u ON i.user_id = u.id
                """;

        return dbConnection.findMany(query, this::mapToInstructor);
    }

    @Override
    public void ensureInstructorExists(Integer instructorId) {
        userRepo.ensureUserExists(instructorId);

        if (findById(instructorId).isEmpty()) {
            throw new IllegalArgumentException("Instructor with ID '" + instructorId + "' does not exist.");
        }
    }

    private Instructor mapToInstructor(ResultSet rs) {
        try {
            return new Instructor(
                    rs.getInt("id"),
                    rs.getString("user_name"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toLocalDateTime() : null,
                    rs.getBoolean("is_active"),
                    rs.getString("bio"),
                    rs.getInt("total_courses_created"),
                    rs.getDouble("rating"),
                    rs.getBoolean("is_verified"));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to map ResultSet to Instructor", e);
        }
    }
}
