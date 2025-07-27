package com.example.repository;

import com.example.model.Submission;
import com.example.model.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaSubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findAllByStudentId(Long studentId);

    List<Submission> findAllByAssignmentId(Long assignmentId);

    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    List<Submission> findAllByStatus(SubmissionStatus status);
}
