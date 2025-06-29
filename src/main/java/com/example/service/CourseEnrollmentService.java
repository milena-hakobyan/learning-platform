package com.example.service;

import com.example.model.Student;

import java.util.List;

public interface CourseEnrollmentService {
    void enrollStudent(Integer courseId, Integer studentId);
    void unenrollStudent(Integer courseId, Integer studentId);
    void ensureStudentEnrollment(Integer studentId, Integer courseId);
    List<Student> getEnrolledStudents(Integer courseId);
}
