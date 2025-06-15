package com.example.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
    private List<Course> coursesCreated = new ArrayList<>();
    private List<Lesson> lessonsCreated = new ArrayList<>();
    private List<Assignment> assignmentsCreated = new ArrayList<>();

    public Instructor(String name, String userName, String email, String password) {
        super(name, userName, email, password, Role.INSTRUCTOR);
    }

    public List<Course> getCoursesCreated() {
        return coursesCreated;
    }

    public List<Assignment> getAssignmentsCreated() {
        return assignmentsCreated;
    }

    public List<Lesson> getLessonsCreated() {
        return lessonsCreated;
    }

    public void setAssignmentsCreated(List<Assignment> assignmentsCreated) {
        this.assignmentsCreated = assignmentsCreated;
    }

    public void setCoursesCreated(List<Course> coursesCreated) {
        this.coursesCreated = coursesCreated;
    }

    public void setLessonsCreated(List<Lesson> lessonsCreated) {
        this.lessonsCreated = lessonsCreated;
    }
}