package com.example.mapper;

import com.example.dto.grade.GradeResponse;
import com.example.dto.grade.GradeSubmissionRequest;
import com.example.model.Grade;
import com.example.model.Submission;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GradeMapper {

    public GradeResponse toDto(Grade grade) {
        GradeResponse response = new GradeResponse();
        response.setId(grade.getId());
        response.setSubmissionId(grade.getSubmission().getId());
        response.setScore(grade.getScore());
        response.setFeedback(grade.getFeedback());
        response.setGradedAt(grade.getGradedAt());
        return response;
    }

    public Grade toEntity(GradeSubmissionRequest request, Submission submission) {
        Grade grade = new Grade();
        grade.setSubmission(submission);
        grade.setScore(request.getScore());
        grade.setFeedback(request.getFeedback());
        grade.setGradedAt(LocalDateTime.now());
        return grade;
    }
}
