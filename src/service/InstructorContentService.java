package com.example.service;

import com.example.model.Assignment;
import com.example.model.Lesson;
import com.example.model.Material;

import java.util.List;

public interface InstructorContentService {
    void createAssignment(Long instructorId, Long courseId, Assignment assignment);
    void deleteAssignment(Long instructorId, Long courseId, Long assignmentId);
    List<Assignment> getAssignmentsCreated(Long instructorId);

    void createLesson(Long instructorId, Long courseId, Lesson lesson);
    void deleteLesson(Long instructorId, Long courseId, Long lessonId);
    List<Lesson> getLessonsCreated(Long instructorId);

    void uploadMaterial(Long instructorId, Long lessonId, Material material);
    void deleteMaterial(Long instructorId, Long lessonId, Long materialId);
}

