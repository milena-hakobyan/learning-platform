package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.model.Course;
import java.util.List;

public interface InstructorCourseService {

    CourseResponse createCourse(Long instructorId, CreateCourseRequest request);

    void deleteCourse(Long courseId, Long instructorId);

    List<CourseResponse> getCoursesCreated(Long instructorId);

}
