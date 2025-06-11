package com.example.Model;

import java.time.LocalDateTime;

public class Grade {
    private Integer gradeId;
    private double score;
    private Integer submissionId;
    private String feedback;
    private LocalDateTime gradedAt;

    public Grade(Integer gradeId, double score, Integer submissionId, String feedback, LocalDateTime gradedAt) {
        this.gradeId = gradeId;
        this.score = score;
        this.submissionId = submissionId;
        this.feedback = feedback;
        this.gradedAt = gradedAt;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public Integer getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Integer submissionId) {
        this.submissionId = submissionId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }
}
