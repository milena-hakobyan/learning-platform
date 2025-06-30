package com.example.service;

import com.example.model.Assignment;
import com.example.model.Lesson;
import com.example.model.Material;

import java.util.List;

public interface InstructorContentService {
    void createAssignment(Integer instructorId, Integer courseId, Assignment assignment);
    void deleteAssignment(Integer instructorId, Integer courseId, Integer assignmentId);
    List<Assignment> getAssignmentsCreated(Integer instructorId);

    void createLesson(Integer instructorId, Integer courseId, Lesson lesson);
    void deleteLesson(Integer instructorId, Integer courseId, Integer lessonId);
    List<Lesson> getLessonsCreated(Integer instructorId);

    void uploadMaterial(Integer instructorId, Integer lessonId, Material material);
    void deleteMaterial(Integer instructorId, Integer lessonId, Integer materialId);
}

