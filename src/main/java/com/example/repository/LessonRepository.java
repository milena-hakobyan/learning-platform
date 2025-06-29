package com.example.repository;

import com.example.model.Lesson;
import com.example.model.Material;

import java.util.List;

public interface LessonRepository extends CrudRepository<Lesson, Integer>{
    List<Lesson> findAllByCourseId(Integer courseId);

    List<Lesson> findAllLessonsByInstructorId(Integer instructorId);

    List<Material> findAllMaterialsByLessonId(Integer lessonId);

    void addMaterial(Integer lessonId, Material material);

    void removeMaterial(Integer lessonId, Integer materialId);

    void ensureStudentAccessToLesson(Integer studentId, Integer lessonId);

    void ensureLessonExists(Integer lessonId);
}
