package com.example.Service;

import com.example.Model.*;

import java.util.List;

public interface InstructorService extends UserService {
    List<Course> getCoursesCreated(String instructorId);

    List<Lesson> getLessonsCreated(String instructorId);

    List<Assignment> getAssignmentsCreated(String instructorId);

    void createCourse(Course course);

    void createAssignment(Course course, Assignment assignment);

    void createLesson(Course course, Lesson lesson);

    void uploadMaterial(Lesson lesson, Material material);

    void deleteMaterial(Lesson lesson, Material material);

    void gradeAssignment(Assignment assignment, Student student, Grade grade);

    void giveFeedback(Assignment assignment, Student student, String feedback);

    void sendAnnouncement(Course course, String title, String message);
}