package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InstructorCourseService {

    CourseResponse createCourse(CreateCourseRequest request);

    void deleteCourse(Long instructorId, Long courseId);

    Page<CourseResponse> getCoursesCreated(Long instructorId, Pageable pageable);

}
