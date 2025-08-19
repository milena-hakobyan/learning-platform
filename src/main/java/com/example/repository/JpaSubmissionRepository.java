package com.example.repository;

import com.example.model.Submission;
import com.example.model.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaSubmissionRepository extends JpaRepository<Submission, Long> {

    Page<Submission> findAllByStudentId(Long studentId, Pageable pageable);

    Page<Submission> findAllByAssignmentId(Long assignmentId, Pageable pageable);

    void deleteAllByAssignmentId(Long assignmentId);

    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    Page<Submission> findAllByStatus(SubmissionStatus status, Pageable pageable);

    boolean existsByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
}
