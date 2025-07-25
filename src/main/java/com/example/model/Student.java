package com.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @MapsId
    private User user;

    private Double progressPercentage;

    private Integer completedCourses;

    private Integer currentCourses;

    @ManyToMany(mappedBy = "enrolledStudents")
    private List<Course> enrolledCourses = new ArrayList<>();
    @OneToMany
    private List<Submission> submissions = new ArrayList<>();

    public Student() {
    }

    public Student(User user) {
        this.user = user;
        this.progressPercentage = 0.0;
        this.completedCourses = 0;
        this.currentCourses = 0;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public Integer getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(Integer completedCourses) {
        this.completedCourses = completedCourses;
    }

    public Integer getCurrentCourses() {
        return currentCourses;
    }

    public void setCurrentCourses(Integer currentCourses) {
        this.currentCourses = currentCourses;
    }

    public void setEnrolledCourses(List<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public void enroll(Course course) {
        enrolledCourses.add(course);
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void addSubmission(Submission submission) {
        if (!submissions.contains(submission)) {
            submissions.add(submission);
            submission.setStudent(this);
        }
    }

    public void removeSubmission(Submission submission) {
        if (submissions.remove(submission)) {
            submission.setStudent(null);
        }
    }
}