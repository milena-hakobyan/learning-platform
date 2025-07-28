package com.example.service;

import com.example.model.Student;

import java.util.List;

public interface CourseEnrollmentService {
    void enrollStudent(Long courseId, Long studentId);

    void unenrollStudent(Long courseId, Long studentId);

    void ensureStudentEnrollment(Long studentId, Long courseId);

    List<Student> getEnrolledStudents(Long courseId);
}
