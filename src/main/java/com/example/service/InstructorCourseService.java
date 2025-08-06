package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.model.Course;
import java.util.List;

public interface InstructorCourseService {

    CourseResponse createCourse(CreateCourseRequest request);

    void deleteCourse(Long instructorId, Long courseId);

    List<CourseResponse> getCoursesCreated(Long instructorId);

}
