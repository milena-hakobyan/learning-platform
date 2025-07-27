package com.example.service;

import com.example.model.Course;
import java.util.List;

public interface InstructorCourseService {

    void createCourse(Course course);

    void deleteCourse(Long instructorId, Long courseId);

    List<Course> getCoursesCreated(Long instructorId);

}
