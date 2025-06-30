package com.example.service;

import com.example.model.Lesson;
import com.example.model.Material;

import java.util.List;

public interface LessonService {
    List<Lesson> getLessonsForCourse(Integer courseId);

    void addLessonToCourse(Integer courseId, Lesson lesson);

    void removeLessonFromCourse(Integer courseId, Integer lessonId);

    void addMaterialToLesson(Integer lessonId, Material material);

    void removeMaterialFromLesson(Integer lessonId, Integer materialId);

}
