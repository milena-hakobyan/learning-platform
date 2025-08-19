package com.example.service;

import com.example.exception.ResourceNotFoundException;
import com.example.model.Course;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaInstructorRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class InstructorAuthorizationServiceImpl implements InstructorAuthorizationService {
    private final JpaCourseRepository courseRepository;
    private final JpaInstructorRepository instructorRepo;

    public InstructorAuthorizationServiceImpl(JpaCourseRepository courseRepository, JpaInstructorRepository instructorRepository) {
        this.courseRepository = courseRepository;
        this.instructorRepo = instructorRepository;
    }

    public Course ensureAuthorizedCourseAccess(Long instructorId, Long courseId) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found with ID: " + instructorId);
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new SecurityException("Instructor is not authorized to access this course");
        }

        return course;
    }

}