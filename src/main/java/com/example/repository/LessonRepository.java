package com.example.repository;

import com.example.model.Lesson;
import com.example.model.Material;

import java.util.List;

public interface LessonRepository extends CrudRepository<Lesson, Integer>{
    List<Lesson> findByCourseId(Integer courseId);

    List<Lesson> findLessonsByInstructorId(Integer instructorId);

    List<Material> findMaterialsByLessonId(Integer lessonId);

    void addMaterial(Integer lessonId, Material material);

    void removeMaterial(Integer lessonId, Integer materialId);

    void ensureStudentAccessToLesson(Integer studentId, Integer lessonId);

    void ensureLessonExists(Integer lessonId);
}
