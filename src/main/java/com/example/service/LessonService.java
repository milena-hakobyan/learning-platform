package com.example.service;

import com.example.dto.lesson.CreateLessonRequest;
import com.example.dto.lesson.LessonResponse;
import com.example.dto.lesson.UpdateLessonRequest;
import com.example.dto.material.CreateMaterialRequest;
import com.example.dto.material.MaterialResponse;
import com.example.model.Lesson;
import com.example.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LessonService {
    Page<LessonResponse> getLessonsForCourse(Long courseId, Pageable pageable);

    LessonResponse addLessonToCourse(Long courseId, CreateLessonRequest request);

    LessonResponse updateLesson(Long lessonId, UpdateLessonRequest request);

    void removeLessonFromCourse(Long courseId, Long lessonId);

    MaterialResponse addMaterialToLesson(Long lessonId, CreateMaterialRequest request);

    void removeMaterialFromLesson(Long lessonId, Long materialId);

    LessonResponse getLessonById(Long lessonId);
}
