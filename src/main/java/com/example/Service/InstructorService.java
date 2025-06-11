package com.example.Service;

import com.example.Model.*;

import java.util.List;
import java.util.Optional;

public interface InstructorService {
    List<Instructor> getAllInstructors();

    Optional<Instructor> getInstructorById(Integer instructorId);

    List<Course> getCoursesCreated(Integer instructorId);

    List<Lesson> getLessonsCreated(Integer instructorId);

    List<Assignment> getAssignmentsCreated(Integer instructorId);

    List<Announcement> getAnnouncementsPosted(Integer instructorId);

    void createCourse(Course course);

    void deleteCourse(Integer courseId);

    void createAssignment(Integer courseId, Assignment assignment);

    void deleteAssignment(Integer courseId, Integer assignmentId);

    void createLesson(Integer courseId, Lesson lesson);

    void deleteLesson(Integer courseId, Integer lessonId);

    void uploadMaterial(Lesson lesson, Material material);

    void deleteMaterial(Lesson lesson, Material material);

    List<Submission> getSubmissionsForAssignment(Integer assignmentId);

    void gradeSubmission(Submission submission, Grade grade);

    void sendAnnouncement(Course course, Integer announcementId, String title, String message);
}