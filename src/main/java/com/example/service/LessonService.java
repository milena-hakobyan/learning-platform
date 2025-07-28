package com.example.service;

import com.example.model.Lesson;
import com.example.model.Material;

import java.util.List;

public interface LessonService {
    List<Lesson> getLessonsForCourse(Long courseId);

    void addLessonToCourse(Long courseId, Lesson lesson);

    void removeLessonFromCourse(Long courseId, Long lessonId);

    void addMaterialToLesson(Long lessonId, Material material);

    void removeMaterialFromLesson(Long lessonId, Long materialId);

}
