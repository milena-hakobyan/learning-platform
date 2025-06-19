package com.example.service;

import com.example.model.*;

import java.util.List;
import java.util.Optional;

public interface InstructorService {
    Optional<Instructor> getInstructorById(Integer instructorId);

    List<Instructor> getAllInstructors();

    List<Course> getCoursesCreated(Integer instructorId);

    List<Lesson> getLessonsCreated(Integer instructorId);

    List<Assignment> getAssignmentsCreated(Integer instructorId);

    List<Announcement> getAnnouncementsPosted(Integer instructorId);

    List<Submission> getSubmissionsForAssignment(Integer instructorId, Integer assignmentId);

    void createCourse(Course course);

    void deleteCourse(Integer instructorId, Integer courseId);

    void createAssignment(Integer instructorId, Integer courseId, Assignment assignment);

    void deleteAssignment(Integer instructorId, Integer courseId, Integer assignmentId);

    void createLesson(Integer instructorId, Integer courseId, Lesson lesson);

    void deleteLesson(Integer instructorId, Integer courseId, Integer lessonId);

    void uploadMaterial(Integer instructorId, Integer lessonId, Material material);

    void deleteMaterial(Integer instructorId, Integer lessonId, Integer materialId);

    void gradeSubmission(Integer instructorId, Integer submissionId, Grade grade);

    void sendAnnouncement(Integer instructorId, Integer courseId, String title, String message);
}