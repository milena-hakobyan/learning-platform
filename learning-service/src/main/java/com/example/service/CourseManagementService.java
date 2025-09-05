package com.example.service;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.course.UpdateCourseRequest;
import com.example.specification.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CourseManagementService {

    CourseResponse createCourse(CreateCourseRequest request);

    CourseResponse updateCourse(Long courseId, UpdateCourseRequest request);

    void deleteCourse(Long courseId);

    CourseResponse getById(Long courseId);

    // Example of fetching a parent entity of One-To-Many relationship with all its children
    CourseResponse getByIdWithLessons(Long courseId);

    Page<CourseResponse> getAllByInstructor(Long instructorId, Pageable pageable);

    Page<CourseResponse> getAllByCategory(String category, Pageable pageable);

    CourseResponse getByTitle(String title);

    Page<CourseResponse> getAll(Pageable pageable);

    Page<AnnouncementResponse> getAnnouncementsForCourse(Long courseId, Pageable pageable);

    Page<CourseResponse> getFilteredCourses(List<SearchCriteria> filters, Pageable pageable);
}