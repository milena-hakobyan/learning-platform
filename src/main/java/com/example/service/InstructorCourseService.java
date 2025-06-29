package com.example.service;

import com.example.model.Course;
import java.util.List;

public interface InstructorCourseService {

    void createCourse(Course course);

    void deleteCourse(Integer instructorId, Integer courseId);

    List<Course> getCoursesCreated(Integer instructorId);

}
