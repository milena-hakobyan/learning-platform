package com.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double score;

    @OneToOne
    @JoinColumn(name = "submission_id", unique = true, nullable = false)
    private Submission submission;

    private String feedback;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    public Grade() {
    }

    public Grade(Long id, Double score, Submission submission, String feedback, LocalDateTime gradedAt) {
        this.id = id;
        this.score = score;
        this.submission = submission;
        this.feedback = feedback;
        this.gradedAt = gradedAt;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Double getScore() { return score; }

    public void setScore(Double score) {
        if (score != null && score < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
        this.score = score;
    }

    public Submission getSubmission() { return submission; }
    public void setSubmission(Submission submission) { this.submission = submission; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public LocalDateTime getGradedAt() { return gradedAt; }
    public void setGradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; }
}