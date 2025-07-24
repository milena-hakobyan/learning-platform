package com.example.model;

import java.time.LocalDateTime;

public class Grade {
    private Integer id;
    private Double score;
    private Integer submissionId; //needs to be removed, this is a 1-to-1 relationship
    private String feedback;
    private LocalDateTime gradedAt;

    public Grade(Integer gradeId, Double score, Integer submissionId, String feedback, LocalDateTime gradedAt) {
        this.id = gradeId;
        this.score = score;
        this.submissionId = submissionId;
        this.feedback = feedback;
        this.gradedAt = gradedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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