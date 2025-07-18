package com.example.model;

import java.time.LocalDateTime;

public class Submission {
    private Integer submissionId;
    private Integer studentId;
    private Integer assignmentId;
    private LocalDateTime submittedAt;
    private String contentLink;
    private SubmissionStatus status;

    public Submission(Integer submissionId, Integer studentId, Integer assignmentId, String contentLink, LocalDateTime submittedAt) {
        this.submissionId = submissionId;
        this.studentId = studentId;
        this.assignmentId = assignmentId;
        this.contentLink = contentLink;
        this.submittedAt = submittedAt;
    }

    public Submission(Integer studentId, Integer assignmentId, String contentLink, LocalDateTime submittedAt) {
        this(null, studentId, assignmentId, contentLink, submittedAt);
    }


    public Integer getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Integer submissionId) {
        this.submissionId = submissionId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Student student) {
        this.studentId = studentId;
    }

    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
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

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
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
                "submissionId='" + submissionId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", assignmentId='" + assignmentId + '\'' +
                ", submittedAt=" + submittedAt +
                ", contentLink='" + contentLink + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}