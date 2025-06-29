package com.example.service;

import com.example.model.Grade;
import com.example.model.Submission;

import java.util.List;

public interface InstructorGradingService {
    void gradeSubmission(Integer instructorId, Integer submissionId, Grade grade);

    List<Submission> getSubmissionsForAssignment(Integer instructorId, Integer assignmentId);
}
