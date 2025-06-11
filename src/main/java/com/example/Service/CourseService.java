package com.example.Service;

import com.example.Model.*;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    void createCourse(Course course);

    void updateCourse(Course course);

    void deleteCourse(Integer courseId);

    void addAssignmentToCourse(Integer courseId, Assignment assignment);

    void removeAssignmentFromCourse(Integer courseId, Integer assignmentId);

    List<Assignment> getAssignmentsForCourse(Integer courseId);

    List<Lesson> getLessonsForCourse(Integer courseId);

    void addLessonToCourse(Integer courseId, Lesson lesson);

    void removeLessonFromCourse(Integer courseId, Integer lessonId);

    void addMaterialToLesson(Integer lessonId, Material material);

    void removeMaterialFromLesson(Integer lessonId, Integer materialId);

    void addMaterialToAssignment(Integer assignmentId, Material material);

    void removeMaterialFromAssignment(Integer assignmentId, Integer materialId);

    void enrollStudent(Integer courseId, Student student);

    List<Announcement> getAnnouncementsForCourse(Integer courseId);

    Optional<Course> getCourseById(Integer courseId);

    // Example of fetching a parent entity of One-To-Many relationship with all its children
    Optional<Course> getByIdWithLessons(Integer courseId);

    List<Course> getCoursesByInstructor(Integer instructorId);

    List<Course> getCoursesByCategory(String category);

    Optional<Course> getCourseByTitle(String title);

    List<Course> getAllCourses();

    List<Student> getEnrolledStudents(Integer courseId);
}