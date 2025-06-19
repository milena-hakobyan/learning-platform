package com.example.repository;

import com.example.model.Assignment;
import com.example.model.Material;
import com.example.utils.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public void update(Assignment assignment) {
        String updateQuery = "UPDATE assignments SET title = ?, description = ?, due_date = ?, max_score = ?, course_id = ? " +
                "WHERE id = ?;";

        dbConnection.execute(updateQuery,
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate().toLocalDate(),
                assignment.getMaxScore(),
                assignment.getCourseId(),
                assignment.getAssignmentId()
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
    public List<Assignment> findByCourseId(Integer courseId) {
        String query = "SELECT * FROM assignments WHERE course_id = ?;";
        return dbConnection.findMany(query, this::mapAssignment, courseId);
    }

    @Override
    public List<Assignment> findAssignmentsByInstructorId(Integer instructorId) {
       String query = """
                    SELECT a.* FROM assignments a
                    JOIN courses c ON a.course_id = c.id
                    WHERE c.instructor_id = ?;
                """;

        return dbConnection.findMany(query, this::mapAssignment, instructorId);
    }

    @Override
    public List<Assignment> findByDueDateBefore(LocalDateTime date) {
        String query = "SELECT * FROM assignments WHERE due_date < ?;";
        return dbConnection.findMany(query, this::mapAssignment, date.toLocalDate());
    }

    @Override
    public Optional<Assignment> findByTitle(String title) {
        String query = "SELECT * FROM assignments WHERE title = ?;";
        return Optional.ofNullable(dbConnection.findOne(query, this::mapAssignment, title));
    }

    @Override
    public void addMaterial(Integer assignmentId, Material material) {
        String insertMaterial = "INSERT INTO materials (title, content_type, category, url, instructor_id) VALUES (?, ?, ?, ?, ?);";
        dbConnection.execute(insertMaterial,
                material.getTitle(),
                material.getContentType(),
                material.getCategory(),
                material.getUrl(),
                material.getInstructorId()
        );

        String findMaterialId = "SELECT id FROM materials WHERE title = ? AND content_type = ? AND url = ? AND instructor_id = ? ORDER BY id DESC LIMIT 1;";
        Integer materialId = dbConnection.findOne(findMaterialId, rs -> {
                    try {
                        return rs.getInt("id");
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to get generated material ID", e);
                    }
                },
                material.getTitle(),
                material.getContentType(),
                material.getUrl(),
                material.getInstructorId()
        );

        String linkQuery = "INSERT INTO assignment_material (assignment_id, material_id) VALUES (?, ?);";
        dbConnection.execute(linkQuery, assignmentId, materialId);
    }

    @Override
    public void removeMaterial(Integer assignmentId, Integer materialId) {
        String query = "DELETE FROM assignment_material WHERE assignment_id = ? AND material_id = ?;";

        dbConnection.execute(query, assignmentId, materialId);
    }


    @Override
    public void ensureAssignmentExists(Integer assignmentId) {
        Objects.requireNonNull(assignmentId, "Assignment ID cannot be null");
        if (findById(assignmentId).isEmpty()) {
            throw new IllegalArgumentException("Assignment with ID '" + assignmentId + "' does not exist.");
        }
    }


    private Assignment mapAssignment(ResultSet rs) {
        try {
            return new Assignment(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getInt("course_id"),
                    rs.getString("description"),
                    rs.getDate("due_date").toLocalDate().atStartOfDay(),
                    rs.getDouble("max_score")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Assignment", e);
        }
    }
}