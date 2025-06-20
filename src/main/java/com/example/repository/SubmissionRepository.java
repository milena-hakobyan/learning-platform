package com.example.repository;

import com.example.model.Submission;
import com.example.model.SubmissionStatus;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends CrudRepository<Submission, Integer> {
    List<Submission> findByStudentId(Integer studentId);

    List<Submission> findByAssignmentId(Integer assignmentId);

    Optional<Submission> findByAssignmentIdAndStudentId(Integer assignmentId, Integer studentId);

    List<Submission> findByStatus(SubmissionStatus status);

    void ensureSubmissionExists(Integer submissionId);
}
