package com.example.service;

import com.example.model.Grade;
import com.example.model.Submission;

import java.util.List;

public interface InstructorGradingService {
    void gradeSubmission(Long instructorId, Long submissionId, Grade grade);

    List<Submission> getSubmissionsForAssignment(Long instructorId, Long assignmentId);
}
