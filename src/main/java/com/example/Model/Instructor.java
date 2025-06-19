package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
    private List<Course> coursesCreated = new ArrayList<>();
    private List<Lesson> lessonsCreated = new ArrayList<>();

    private String bio;
    private int totalCoursesCreated;
    private double rating;
    private boolean isVerified;

    public Instructor(Integer userId, String userName, String firstName, String lastName, String email, String password,
                      LocalDateTime lastLogin, boolean isActive, String bio, int totalCoursesCreated,
                      double rating, boolean isVerified) {
        super(userId, userName, firstName, lastName, email, password, Role.INSTRUCTOR, lastLogin, isActive);
        this.bio = bio;
        this.totalCoursesCreated = totalCoursesCreated;
        this.rating = rating;
        this.isVerified = isVerified;
    }

    public Instructor(String userName, String firstName, String lastName, String email, String password,
                      LocalDateTime lastLogin, String bio) {
        super(userName, firstName, lastName, email, password, Role.INSTRUCTOR, lastLogin);
        this.bio = bio;
        this.totalCoursesCreated = 0;
        this.rating = 0.0;
        this.isVerified = false;
    }

    public List<Course> getCoursesCreated() {
        return coursesCreated;
    }

    public void setCoursesCreated(List<Course> coursesCreated) {
        this.coursesCreated = coursesCreated;
    }

    public List<Lesson> getLessonsCreated() {
        return lessonsCreated;
    }

    public void setLessonsCreated(List<Lesson> lessonsCreated) {
        this.lessonsCreated = lessonsCreated;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getTotalCoursesCreated() {
        return totalCoursesCreated;
    }

    public void setTotalCoursesCreated(int totalCoursesCreated) {
        this.totalCoursesCreated = totalCoursesCreated;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
