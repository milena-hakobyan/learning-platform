package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private Double progressPercentage;
    private Integer completedCourses;
    private Integer currentCourses;

    private List<Course> enrolledCourses = new ArrayList<>();
    private List<Submission> submissions = new ArrayList<>();

    public Student(Integer userId, String userName, String firstName, String lastName, String email, String password,
                   LocalDateTime lastLogin, boolean isActive, Double progressPercentage, Integer completedCourses, Integer currentCourses) {
        super(userId, userName, firstName, lastName, email, password, Role.STUDENT, lastLogin, isActive);
        this.progressPercentage = progressPercentage;
        this.completedCourses = completedCourses;
        this.currentCourses = currentCourses;
    }


    public Student(String userName, String firstName, String lastName, String email, String password,
                   LocalDateTime lastLogin) {
        super(userName, firstName, lastName, email, password, Role.STUDENT, lastLogin);
        this.progressPercentage = 0.0;
        this.completedCourses = 0;
        this.currentCourses = 0;
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
}