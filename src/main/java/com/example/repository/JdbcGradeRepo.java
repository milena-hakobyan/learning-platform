package com.example.repository;

import com.example.model.Assignment;
import com.example.model.Grade;
import com.example.utils.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcGradeRepo implements GradeRepository {
    private final DatabaseConnection dbConnection;

    public JdbcGradeRepo(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Grade save(Grade entity) {
        if (entity == null) throw new IllegalArgumentException("Grade cannot be null");

        String query = """
                INSERT INTO grades (submission_id, score, feedback) VALUES (?, ?, ?)
                RETURNING id, submission_id, score, feedback
                """;

        return dbConnection.findOne(query, this::mapGrade, entity.getSubmissionId(), entity.getScore(), entity.getFeedback());
    }

    @Override
    public Grade update(Grade entity) {
        String updateQuery = """
                UPDATE grades
                SET submission_id = ?, score = ?, feedback = ?
                WHERE id = ?
                RETURNING *;
                """;

        return dbConnection.findOne(updateQuery, this::mapGrade,
                entity.getSubmissionId(),
                entity.getScore(),
                entity.getFeedback(),
                entity.getId()
        );
    }


    @Override
    public void delete(Integer gradeId) {
        String query = "DELETE FROM grades WHERE id = ?";
        dbConnection.execute(query, gradeId);
    }

    @Override
    public Optional<Grade> findById(Integer gradeId) {
        String query = "SELECT * FROM grades WHERE id = ?;";
        return Optional.ofNullable(dbConnection.findOne(query, this::mapGrade, gradeId));
    }

    @Override
    public List<Grade> findAllGradesByStudentId(Integer studentId) {
        String query = """
                SELECT g.id, g.score, g.submission_id, g.feedback, g.graded_at
                FROM grades g
                JOIN submissions s ON g.submission_id = s.id
                WHERE s.student_id = ?
                """;

        return dbConnection.findMany(query, this::mapGrade, studentId);
    }

    @Override
    public Optional<Grade> findByAssignmentIdAndStudentId(Integer assignmentId, Integer studentId) {
        String query = """
                    SELECT g.id, g.score, g.submission_id, g.feedback, g.graded_at
                    FROM grades g
                        JOIN submissions s ON g.submission_id = s.id
                    WHERE s.assignment_id = ? AND s.student_id = ?
                """;

        return Optional.ofNullable(dbConnection.findOne(query, this::mapGrade, assignmentId, studentId));
    }

    @Override
    public Map<Assignment, Grade> findGradesByStudentIdForCourse(Integer studentId, Integer courseId) {
        String query = """
                    SELECT g.id AS grade_id, g.score, g.submission_id, g.feedback, g.graded_at,
                           a.id AS assignment_id, a.title, a.description, a.due_date, a.course_id
                    FROM grades g
                        JOIN submissions s ON g.submission_id = s.id
                        JOIN assignments a ON s.assignment_id = a.id
                    WHERE s.student_id = ? AND a.course_id = ?
                """;

        return dbConnection.findMany(query, this::mapAssignmentGrade, studentId, courseId).stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    @Override
    public List<Grade> findAll() {
        String query = "SELECT * FROM grades;";
        return dbConnection.findMany(query, this::mapGrade);
    }

    private Grade mapGrade(ResultSet rs) {
        try {
            return new Grade(
                    rs.getInt("id"),
                    rs.getDouble("score"),
                    rs.getInt("submission_id"),
                    rs.getString("feedback"),
                    rs.getTimestamp("graded_at").toLocalDateTime()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Grade", e);
        }
    }

    private Map.Entry<Assignment, Grade> mapAssignmentGrade(ResultSet rs) {
        try {
            Assignment assignment = new Assignment(
                    rs.getInt("assignment_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getTimestamp("due_date").toLocalDateTime(),
                    rs.getInt("max_score"),
                    rs.getInt("course_id")
            );

            Grade grade = new Grade(
                    rs.getInt("grade_id"),
                    rs.getDouble("score"),
                    rs.getInt("submission_id"),
                    rs.getString("feedback"),
                    rs.getTimestamp("graded_at").toLocalDateTime()
            );

            return Map.entry(assignment, grade);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to map Assignment and Grade", e);
        }
    }

}