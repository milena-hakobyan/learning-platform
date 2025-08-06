package com.example.service;

import com.example.model.*;

import java.util.List;
import java.util.Optional;

public interface CourseManagementService {

    void createCourse(Course course);

    void updateCourse(Course course);

    void deleteCourse(Long courseId);

    Optional<Course> getCourseById(Long courseId);

    // Example of fetching a parent entity of One-To-Many relationship with all its children
    Optional<Course> getByIdWithLessons(Long courseId);

    List<Course> getCoursesByInstructor(Long instructorId);

    List<Course> getCoursesByCategory(String category);

    Optional<Course> getCourseByTitle(String title);

    List<Course> getAllCourses();

    List<Announcement> getAnnouncementsForCourse(Long courseId);
}