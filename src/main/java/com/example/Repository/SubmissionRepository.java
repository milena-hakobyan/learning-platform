package com.example.Repository;

import com.example.Model.Submission;

import java.util.List;

public interface SubmissionRepository extends CrudRepository<Submission, String> {
    List<Submission> findByStudentId(String studentId);

    List<Submission> findByAssignmentId(String assignmentId);

    Submission findByAssignmentIdAndStudentId(String assignmentId, String studentId);

    List<Submission> findByStatus(String status); // e.g., submitted, graded, late
}
