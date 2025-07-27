package com.example.service;

import com.example.model.Course;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class InstructorAuthorizationServiceImpl implements InstructorAuthorizationService {
    private final CourseManagementService courseService;

    public InstructorAuthorizationServiceImpl(CourseManagementService courseService) {
        this.courseService = courseService;
    }

    @Override
    public Course ensureAuthorizedCourseAccess(Long instructorId, Long courseId) {
        Objects.requireNonNull(instructorId);
        Objects.requireNonNull(courseId);

        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new SecurityException("Instructor is not authorized to access this course");
        }

        return course;
    }
}