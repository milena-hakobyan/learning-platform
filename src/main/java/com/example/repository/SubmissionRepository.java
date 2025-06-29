package com.example.repository;

import com.example.model.Submission;
import com.example.model.SubmissionStatus;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends CrudRepository<Submission, Integer> {
    List<Submission> findAllByStudentId(Integer studentId);

    List<Submission> findAllByAssignmentId(Integer assignmentId);

    Optional<Submission> findByAssignmentIdAndStudentId(Integer assignmentId, Integer studentId);

    List<Submission> findAllByStatus(SubmissionStatus status);

    void ensureSubmissionExists(Integer submissionId);
}
