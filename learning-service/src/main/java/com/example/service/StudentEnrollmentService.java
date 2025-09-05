package com.example.service;

import com.example.dto.course.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentEnrollmentService {
    Page<CourseResponse> getEnrolledCourses(Long studentId, Pageable pageable);
    Page<CourseResponse> browseAvailableCourses(Pageable pageable);
    void enrollInCourse(Long studentId, Long courseId);
    void dropCourse(Long studentId, Long courseId);

}
