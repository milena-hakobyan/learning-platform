package com.example.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "user_id")
    private Long userId;

    private Double progressPercentage;

    private Integer completedCourses;

    private Integer currentCourses;

    @ManyToMany(mappedBy = "enrolledStudents")
    private List<Course> enrolledCourses = new ArrayList<>();
    @OneToMany
    private List<Submission> submissions = new ArrayList<>();

    public Student(Long userId) {
        this.userId = userId;
        this.progressPercentage = 0.0;
        this.completedCourses = 0;
        this.currentCourses = 0;
    }

    public void enroll(Course course) {
        enrolledCourses.add(course);
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