package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"assignment_id", "student_id"})
})
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "content_link", nullable = false)
    private String contentLink;

    @Enumerated(EnumType.STRING)
    @Column
    private SubmissionStatus status;

    public Submission() {
    }

    public Submission(Long id, Student student, Assignment assignment, String contentLink, LocalDateTime submittedAt) {
        this.id = id;
        this.student = student;
        this.assignment = assignment;
        this.contentLink = contentLink;
        this.submittedAt = submittedAt;
        this.status = SubmissionStatus.SUBMITTED; // default if needed
    }

    public Submission(Student student, Assignment assignment, String contentLink, LocalDateTime submittedAt) {
        this(null, student, assignment, contentLink, submittedAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getContentLink() {
        return contentLink;
    }

    public void setContentLink(String contentLink) {
        this.contentLink = contentLink;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", student=" + (student != null ? student.getId() : null) +
                ", assignment=" + (assignment != null ? assignment.getId() : null) +
                ", submittedAt=" + submittedAt +
                ", contentLink='" + contentLink + '\'' +
                ", status=" + status +
                '}';
    }
}
