package com.example.Repository;

import com.example.Model.Lesson;
import com.example.Model.Material;

import java.util.List;

public interface LessonRepository extends CrudRepository<Lesson, Integer>{
    List<Lesson> findByCourseId(Integer courseId);

    List<Lesson> findLessonsByInstructorId(Integer instructorId);

    List<Material> findMaterialsByLessonId(Integer lessonId);

    void addMaterial(Integer lessonId, Material material);

    void removeMaterial(Integer lessonId, Integer materialId);

    boolean verifyStudentAccessToLesson(Integer studentId, Integer lessonId);
}
