package com.example.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private List<Course> enrolledCourses = new ArrayList<>();
    private List<Submission> submissions = new ArrayList<>();

    public Student(String name, String userName, String email, String password) {
        super(name, userName, email, password, Role.STUDENT);
    }

    public void enroll(Course course){
        enrolledCourses.add(course);
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }
}