package com.example.repository;

import com.example.model.Assignment;
import com.example.model.Material;
import com.example.utils.DatabaseConnection;
import com.example.utils.DatabaseException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JdbcAssignmentRepo implements AssignmentRepository {
    private final DatabaseConnection dbConnection;

    public JdbcAssignmentRepo(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Assignment save(Assignment assignment) {
        String query = "INSERT INTO assignments (title, description, due_date, max_score, course_id) VALUES (?, ?, ?, ?, ?) RETURNING *";

        return dbConnection.findOne(query, this::mapAssignment, assignment.getTitle(), assignment.getDescription(),
                assignment.getDueDate().toLocalDate(), assignment.getMaxScore(), assignment.getCourseId());
    }

    @Override
    public Assignment update(Assignment assignment) {
        String updateQuery = "UPDATE assignments SET title = ?, description = ?, due_date = ?, max_score = ?, course_id = ? " +
                "WHERE id = ?;";

        return dbConnection.findOne(updateQuery, this::mapAssignment,
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate().toLocalDate(),
                assignment.getMaxScore(),
                assignment.getCourseId(),
                assignment.getId()
        );
    }

    @Override
    public void delete(Integer assignmentId) {
        String query = "DELETE FROM assignments WHERE id = ?;";
        dbConnection.execute(query, assignmentId);
    }

    @Override
    public Optional<Assignment> findById(Integer assignmentId) {
        String query = "SELECT * FROM assignments WHERE id = ?;";

        return Optional.ofNullable(dbConnection.findOne(query, this::mapAssignment, assignmentId));
    }

    @Override
    public List<Assignment> findAll() {
        String query = "SELECT * FROM assignments;";

        return dbConnection.findMany(query, this::mapAssignment);
    }

    @Override
    public List<Assignment> findAllByCourseId(Integer courseId) {
        String query = "SELECT * FROM assignments WHERE course_id = ?;";
        return dbConnection.findMany(query, this::mapAssignment, courseId);
    }

    @Override
    public List<Assignment> findAllAssignmentsByInstructorId(Integer instructorId) {
        String query = """
                    SELECT a.* FROM assignments a
                    JOIN courses c ON a.course_id = c.id
                    WHERE c.instructor_id = ?;
                """;

        return dbConnection.findMany(query, this::mapAssignment, instructorId);
    }

    @Override
    public List<Assignment> findAllByDueDateBefore(LocalDateTime date) {
        String query = "SELECT * FROM assignments WHERE due_date < ?;";
        return dbConnection.findMany(query, this::mapAssignment, date.toLocalDate());
    }

    @Override
    public List<Material> findMaterialsByAssignmentId(Integer assignmentId) {
        String sql = """
                    SELECT m.id, m.title, m.content_type, m.category, m.url, m.instructor_id, m.upload_date
                    FROM materials m
                    JOIN assignment_materials am ON m.id = am.material_id
                    WHERE am.assignment_id = ?;
                """;
        return dbConnection.findMany(sql, this::mapMaterial, assignmentId);
    }


    @Override
    public Optional<Assignment> findByTitle(String title) {
        String query = "SELECT * FROM assignments WHERE title = ?;";
        return Optional.ofNullable(dbConnection.findOne(query, this::mapAssignment, title));
    }

    @Override
    public void addMaterial(Integer assignmentId, Material material) {
        String insertMaterial = """
                INSERT INTO materials (title, content_type, category, url, instructor_id)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id;
                """;

        String linkQuery = """ 
                INSERT INTO assignment_materials (assignment_id, material_id)
                VALUES (?, ?);
                """;

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement insertMaterialStmt = conn.prepareStatement(insertMaterial);
                    PreparedStatement linkQueryStmt = conn.prepareStatement(linkQuery)
            ) {
                insertMaterialStmt.setString(1, material.getTitle());
                insertMaterialStmt.setString(2, material.getContentType());
                insertMaterialStmt.setString(3, material.getCategory());
                insertMaterialStmt.setString(4, material.getUrl());
                insertMaterialStmt.setObject(5, material.getInstructorId());

                ResultSet rs = insertMaterialStmt.executeQuery();
                if (!rs.next()) {
                    throw new SQLException("Failed to insert material, no ID returned.");
                }
                int materialId = rs.getInt("id");

                linkQueryStmt.setInt(1, assignmentId);
                linkQueryStmt.setInt(2, materialId);
                linkQueryStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Transaction failed and was rolled back: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Connection or rollback failed: " + e.getMessage(), e);
        }
    }


    @Override
    public void removeMaterial(Integer assignmentId, Integer materialId) {
        String query = "DELETE FROM assignment_materials WHERE assignment_id = ? AND material_id = ?;";

        dbConnection.execute(query, assignmentId, materialId);
    }


    @Override
    public void ensureAssignmentExists(Integer assignmentId) {
        Objects.requireNonNull(assignmentId, "Assignment ID cannot be null");
        if (findById(assignmentId).isEmpty()) {
            throw new IllegalArgumentException("Assignment with ID '" + assignmentId + "' does not exist.");
        }
    }

    private Material mapMaterial(ResultSet rs) {
        try {
            return new Material(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content_type"),
                    rs.getString("category"),
                    rs.getString("url"),
                    rs.getInt("instructor_id"),
                    rs.getTimestamp("upload_date").toLocalDateTime()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Material", e);
        }
    }

    private Assignment mapAssignment(ResultSet rs) {
        try {
            return new Assignment(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDate("due_date").toLocalDate().atStartOfDay(),
                    rs.getDouble("max_score"),
                    rs.getInt("course_id")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Assignment", e);
        }
    }
}