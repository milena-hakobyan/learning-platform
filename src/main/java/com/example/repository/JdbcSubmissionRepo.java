package com.example.repository;

import com.example.model.Submission;
import com.example.model.SubmissionStatus;
import com.example.utils.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Repository
public class JdbcSubmissionRepo implements SubmissionRepository {
    private final DatabaseConnection dbConnection;

    public JdbcSubmissionRepo(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Submission save(Submission submission) {
        String query = """
                    INSERT INTO submissions (assignment_id, student_id, content_link)
                    VALUES (?, ?, ?) RETURNING *;
                """;

        return dbConnection.findOne(query, this::mapSubmission, submission.getAssignmentId(),
                submission.getStudentId(), submission.getContentLink());
    }

    @Override
    public Submission update(Submission submission) {
        String sql = """
                    UPDATE submissions
                    SET assignment_id = ?, student_id = ?, content_link = ?, status = ?, submitted_at = ?
                    WHERE id = ?
                    RETURNING *;
                """;

        return dbConnection.findOne(sql, this::mapSubmission,
                submission.getAssignmentId(),
                submission.getStudentId(),
                submission.getContentLink(),
                submission.getStatus() != null ? submission.getStatus().name() : null,
                submission.getSubmittedAt(),
                submission.getSubmissionId()
        );
    }

    @Override
    public void delete(Integer submissionId) {
        String sql = "DELETE FROM submissions WHERE id = ?;";

        dbConnection.execute(sql, submissionId);
    }

    @Override
    public Optional<Submission> findById(Integer submissionId) {
        String sql = "SELECT * FROM submissions WHERE id = ?;";
        Submission submission = dbConnection.findOne(sql, this::mapSubmission, submissionId);

        return Optional.ofNullable(submission);
    }

    @Override
    public List<Submission> findAll() {
        String sql = "SELECT * FROM submissions;";

        return dbConnection.findMany(sql, this::mapSubmission);
    }

    @Override
    public List<Submission> findAllByStudentId(Integer studentId) {
        String sql = "SELECT * FROM submissions WHERE student_id = ?;";

        return dbConnection.findMany(sql, this::mapSubmission, studentId);
    }

    @Override
    public List<Submission> findAllByAssignmentId(Integer assignmentId) {
        String sql = "SELECT * FROM submissions WHERE assignment_id = ?;";

        return dbConnection.findMany(sql, this::mapSubmission, assignmentId);
    }

    @Override
    public Optional<Submission> findByAssignmentIdAndStudentId(Integer assignmentId, Integer studentId) {
        String sql = """
                    SELECT * FROM submissions
                    WHERE assignment_id = ? AND student_id = ?
                    LIMIT 1;
                """;

        Submission submission = dbConnection.findOne(sql, this::mapSubmission, assignmentId, studentId);
        return Optional.ofNullable(submission);
    }

    @Override
    public List<Submission> findAllByStatus(SubmissionStatus status) {
        String sql = "SELECT * FROM submissions WHERE status = ?;";

        return dbConnection.findMany(sql, this::mapSubmission, status.name().toLowerCase());
    }

    @Override
    public void ensureSubmissionExists(Integer submissionId) {
        Objects.requireNonNull(submissionId, "Submission ID cannot be null");
        if (findById(submissionId).isEmpty()) {
            throw new IllegalArgumentException("Submission with ID '" + submissionId + "' does not exist.");
        }
    }

    private Submission mapSubmission(ResultSet rs) {
        try {
            Submission submission = new Submission(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getInt("assignment_id"),
                    rs.getString("content_link"),
                    rs.getTimestamp("submitted_at").toLocalDateTime()
            );

            String statusStr = rs.getString("status");
            if (statusStr != null) {
                submission.setStatus(SubmissionStatus.valueOf(statusStr));
            }

            return submission;
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Submission", e);
        }
    }
}
