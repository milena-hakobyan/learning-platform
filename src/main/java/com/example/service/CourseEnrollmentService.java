package com.example.service;

import com.example.dto.student.StudentResponse;
import com.example.model.Student;

import java.util.List;

public interface CourseEnrollmentService {
    void enrollStudent(Long courseId, Long studentId);

    void unenrollStudent(Long courseId, Long studentId);

    void ensureStudentEnrollment(Long studentId, Long courseId);

    List<StudentResponse> getEnrolledStudents(Long courseId);
}
