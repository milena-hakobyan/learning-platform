package com.example.Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Submission {
    private String submissionId;
    private Student student;
    private Assignment assignment;
    private LocalDateTime submittedAt;
    private String contentLink;
    private Grade grade;
    private String instructorRemarks;
    private SubmissionStatus status;  //"submitted", "late", "graded"

    public Submission(Student student, Assignment assignment, String contentLink, LocalDateTime submittedAt) {
        this.submissionId = UUID.randomUUID().toString();
        this.student = student;
        this.assignment = assignment;
        this.contentLink = contentLink;
        this.submittedAt = submittedAt;
        this.grade = null; // grade will be set later after insturctor's evaluation
        this.instructorRemarks = null;

        if (submittedAt.isAfter(assignment.getDueDate())) {
            this.status = SubmissionStatus.LATE;
        } else {
            this.status = SubmissionStatus.SUBMITTED;
        }
    }


    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
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

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public String getInstructorRemarks() {
        return instructorRemarks;
    }

    public void setInstructorRemarks(String instructorRemarks) {
        this.instructorRemarks = instructorRemarks;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "submissionId='" + submissionId + '\'' +
                ", student='" + student.getUserName() + '\'' +
                ", assignment='" + assignment.getTitle() + '\'' +
                ", submittedAt=" + submittedAt +
                ", contentLink='" + contentLink + '\'' +
                ", grade=" + (grade != null ? grade.getScore() : "Not graded") + '\'' +
                ", feedback: " +  (grade != null ? grade.getFeedback(): "No feedback") + '\'' +
                ", instructorRemarks='" + (instructorRemarks != null ? instructorRemarks : "None") + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
