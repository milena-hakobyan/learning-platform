package com.example.service;

import com.example.model.Course;

import java.util.Objects;

public class InstructorAuthorizationServiceImpl implements InstructorAuthorizationService {
    private final CourseManagementService courseService;

    public InstructorAuthorizationServiceImpl(CourseManagementService courseService) {
        this.courseService = courseService;
    }

    @Override
    public Course ensureAuthorizedCourseAccess(Integer instructorId, Integer courseId) {
        Objects.requireNonNull(instructorId);
        Objects.requireNonNull(courseId);

        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new SecurityException("Instructor is not authorized to access this course");
        }

        return course;
    }
}
