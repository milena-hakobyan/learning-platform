package com.example.service;

import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.assignment.CreateAssignmentRequest;
import com.example.dto.assignment.UpdateAssignmentRequest;
import com.example.dto.lesson.CreateLessonRequest;
import com.example.dto.lesson.LessonResponse;
import com.example.dto.lesson.UpdateLessonRequest;
import com.example.dto.material.CreateMaterialRequest;
import com.example.dto.material.MaterialResponse;
import com.example.model.Assignment;
import com.example.model.Lesson;
import com.example.model.Material;

import java.util.List;

public interface InstructorContentService {
    AssignmentResponse createAssignment(Long instructorId, Long courseId, CreateAssignmentRequest request);
    AssignmentResponse updateAssignment(Long instructorId, Long assignmentId, UpdateAssignmentRequest request);
    void deleteAssignment(Long instructorId, Long courseId, Long assignmentId);

    LessonResponse createLesson(Long instructorId, Long courseId, CreateLessonRequest request);
    LessonResponse updateLesson(Long instructorId,Long lessonId, UpdateLessonRequest request);
    void deleteLesson(Long instructorId, Long courseId, Long lessonId);

    MaterialResponse addMaterialToLesson(Long instructorId, Long lessonId, CreateMaterialRequest request);
    void deleteMaterialFromLesson(Long instructorId, Long lessonId, Long materialId);

    MaterialResponse addMaterialToAssignment(Long instructorId, Long assignmentId, CreateMaterialRequest request);
    void deleteMaterialFromAssignment(Long instructorId, Long assignmentId, Long materialId);
}