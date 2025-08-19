package com.example.service;

import com.example.dto.student.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CourseEnrollmentService {
    void enrollStudent(Long courseId, Long studentId);

    void unenrollStudent(Long courseId, Long studentId);

    void ensureStudentEnrollment(Long studentId, Long courseId);

    Page<StudentResponse> getEnrolledStudents(Long courseId, Pageable pageable);
}
