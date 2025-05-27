package com.example.Model;

import java.time.LocalDateTime;

public class Grade {
    private double score;
    private String feedback;
    private LocalDateTime gradedAt;

    public Grade(double score, String feedback, LocalDateTime gradedAt) {
        this.score = score;
        this.feedback = feedback;
        this.gradedAt = gradedAt;
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
