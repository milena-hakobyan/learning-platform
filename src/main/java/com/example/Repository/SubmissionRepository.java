package com.example.Repository;

import com.example.Model.Submission;
import com.example.Model.SubmissionStatus;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends CrudRepository<Submission, Integer> {
    List<Submission> findByStudentId(Integer studentId);

    List<Submission> findByAssignmentId(Integer assignmentId);

    Optional<Submission> findByAssignmentIdAndStudentId(Integer assignmentId, Integer studentId);

    List<Submission> findByStatus(SubmissionStatus status);
}
