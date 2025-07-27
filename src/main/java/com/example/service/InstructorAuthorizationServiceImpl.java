package com.example.service;

import com.example.model.Course;
import com.example.repository.JpaInstructorRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class InstructorAuthorizationServiceImpl implements InstructorAuthorizationService {
    private final CourseManagementService courseService;
    private final JpaInstructorRepository instructorRepo;

    public InstructorAuthorizationServiceImpl(CourseManagementService courseService, JpaInstructorRepository instructorRepository) {
        this.courseService = courseService;
        this.instructorRepo = instructorRepository;
    }

    public Course ensureAuthorizedCourseAccess(Long instructorId, Long courseId) {
        Objects.requireNonNull(instructorId);
        Objects.requireNonNull(courseId);

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found with ID: " + instructorId);
        }

        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new SecurityException("Instructor is not authorized to access this course");
        }

        return course;
    }

}