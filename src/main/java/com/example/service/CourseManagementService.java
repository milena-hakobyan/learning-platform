package com.example.service;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.course.UpdateCourseRequest;
import com.example.model.*;

import java.util.List;
import java.util.Optional;

public interface CourseManagementService {

    CourseResponse createCourse(CreateCourseRequest request);

    CourseResponse updateCourse(Long courseId, UpdateCourseRequest request);

    void deleteCourse(Long courseId);

    Optional<CourseResponse> getById(Long courseId);

    // Example of fetching a parent entity of One-To-Many relationship with all its children
    Optional<CourseResponse> getByIdWithLessons(Long courseId);

    List<CourseResponse> getAllByInstructor(Long instructorId);

    List<CourseResponse> getAllByCategory(String category);

    Optional<CourseResponse> getByTitle(String title);

    List<CourseResponse> getAll();

    List<AnnouncementResponse> getAnnouncementsForCourse(Long courseId);
}