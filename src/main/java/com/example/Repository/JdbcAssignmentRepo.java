package com.example.Repository;

import com.example.Model.Assignment;
import com.example.Model.Material;
import com.example.Utils.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class JdbcAssignmentRepo implements AssignmentRepository {
    private final DatabaseConnection dbConnection;

    public JdbcAssignmentRepo(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Assignment save(Assignment assignment) {
        if (assignment == null) throw new IllegalArgumentException("Assignment cannot be null");

        String query = "INSERT INTO assignments (title, description, due_date, max_score, course_id) VALUES (?, ?, ?, ?, ?) RETURNING *";

        return dbConnection.findOne(query, this::mapAssignment, assignment.getTitle(), assignment.getDescription(),
                assignment.getDueDate().toLocalDate(), assignment.getMaxScore(), assignment.getCourseId());
    }

    @Override
    public void update(Assignment assignment) {
        if (assignment == null || assignment.getAssignmentId() == null) {
            throw new IllegalArgumentException("Assignment or Assignment ID cannot be null");
        }

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
        if (assignmentId == null) throw new IllegalArgumentException("Assignment ID cannot be null");

        String query = "DELETE FROM assignments WHERE id = ?;";
        dbConnection.execute(query, assignmentId);
    }

    @Override
    public Optional<Assignment> findById(Integer assignmentId) {
        if (assignmentId == null) throw new IllegalArgumentException("Assignment ID cannot be null");

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
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");

        String query = "SELECT * FROM assignments WHERE course_id = ?;";
        return dbConnection.findMany(query, this::mapAssignment, courseId);
    }

    @Override
    public List<Assignment> findAssignmentsByInstructorId(Integer instructorId) {
        if (instructorId == null) throw new IllegalArgumentException("Instructor ID cannot be null");

        String query = """
                    SELECT a.* FROM assignments a
                    JOIN courses c ON a.course_id = c.id
                    WHERE c.instructor_id = ?;
                """;

        return dbConnection.findMany(query, this::mapAssignment, instructorId);
    }

    @Override
    public List<Assignment> findByDueDateBefore(LocalDateTime date) {
        if (date == null) throw new IllegalArgumentException("Date cannot be null");

        String query = "SELECT * FROM assignments WHERE due_date < ?;";
        return dbConnection.findMany(query, this::mapAssignment, date.toLocalDate());
    }

    @Override
    public Optional<Assignment> findByTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be null or blank");

        String query = "SELECT * FROM assignments WHERE title = ?;";
        return Optional.ofNullable(dbConnection.findOne(query, this::mapAssignment, title));
    }

    @Override
    public void addMaterial(Integer assignmentId, Material material) {
        if (assignmentId == null) throw new IllegalArgumentException("Assignment Id cannot be null");
        if (material == null) throw new IllegalArgumentException("Material cannot be null");

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