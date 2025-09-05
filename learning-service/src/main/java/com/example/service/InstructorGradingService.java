package com.example.service;

import com.example.dto.grade.GradeSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.model.Grade;
import com.example.model.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InstructorGradingService {
    void gradeSubmission(Long instructorId, Long SubmissionId, GradeSubmissionRequest dto);

    Page<SubmissionResponse> getSubmissionsForAssignment(Long instructorId, Long assignmentId, Pageable pageable);
}
