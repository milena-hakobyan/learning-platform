package com.example.Repository;

import com.example.Model.Submission;
import com.example.Model.SubmissionStatus;
import com.example.Utils.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public void update(Submission submission) {
        if (submission == null || submission.getSubmissionId() == null) {
            throw new IllegalArgumentException("Submission or Submission ID cannot be null");
        }

        String sql = """
                    UPDATE submissions
                    SET assignment_id = ?, student_id = ?, content_link = ?, grade_id = ?, status = ?, submitted_at = ?
                    WHERE submission_id = ?;
                """;

        dbConnection.execute(
                sql,
                submission.getAssignmentId(),
                submission.getStudentId(),
                submission.getContentLink(),
                submission.getGradeId(),
                submission.getStatus(),
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
        String sql = "SELECT * FROM submissions WHERE submission_id = ?;";
        Submission submission = dbConnection.findOne(sql, this::mapSubmission, submissionId);

        return Optional.ofNullable(submission);
    }

    @Override
    public List<Submission> findAll() {
        String sql = "SELECT * FROM submissions;";

        return dbConnection.findMany(sql, this::mapSubmission);
    }

    @Override
    public List<Submission> findByStudentId(Integer studentId) {
        String sql = "SELECT * FROM submissions WHERE student_id = ?;";

        return dbConnection.findMany(sql, this::mapSubmission, studentId);
    }

    @Override
    public List<Submission> findByAssignmentId(Integer assignmentId) {
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
    public List<Submission> findByStatus(SubmissionStatus status) {
        String sql = "SELECT * FROM submissions WHERE status = ?;";

        return dbConnection.findMany(sql, this::mapSubmission, status.name().toLowerCase());
    }

    private Submission mapSubmission(ResultSet rs) {
        try {
            Submission submission = new Submission(
                    rs.getInt("submission_id"),
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
