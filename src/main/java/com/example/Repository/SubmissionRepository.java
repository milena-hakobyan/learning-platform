package com.example.Repository;

import com.example.Model.Submission;
import com.example.Model.SubmissionStatus;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends CrudRepository<Submission, String> {
    List<Submission> findByStudentId(String studentId);

    List<Submission> findByAssignmentId(String assignmentId);

    Optional<Submission> findByAssignmentIdAndStudentId(String assignmentId, String studentId);

    List<Submission> findByStatus(SubmissionStatus status); // e.g., submitted, graded, late
}
