package com.example.service;

import com.example.dto.grade.GradeSubmissionRequest;
import com.example.dto.grade.GradeResponse;
import com.example.dto.submission.SubmissionResponse;
import com.example.model.Grade;

import java.util.List;

public interface InstructorGradingService {

    GradeResponse gradeSubmission(Long instructorId, Long submissionId, GradeSubmissionRequest dto);

    List<SubmissionResponse> getSubmissionsForAssignment(Long instructorId, Long assignmentId);
}
