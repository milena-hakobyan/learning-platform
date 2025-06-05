package com.example.Service;

import com.example.Model.*;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    void createCourse(Course course);

    void updateCourse(Course course);

    void deleteCourse(String courseId);

    void addAssignmentToCourse(String courseId, Assignment assignment);

    void removeAssignmentFromCourse(String courseId, String assignmentId);

    void addLessonToCourse(String courseId, Lesson lesson);

    void removeLessonFromCourse(String courseId, String lessonId);

    void enrollStudent(String courseId, Student student);

    Optional<Course> getCourseById(String courseId);

    List<Course> getCoursesByInstructor(String instructorId);

    List<Course> getCoursesByCategory(String category);

    List<Course> getCoursesByTags(List<String> tags);

    Optional<Course> getCourseByTitle(String title);

    List<Course> getAllCourses();

}
