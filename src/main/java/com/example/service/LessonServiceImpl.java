package com.example.service;

import com.example.model.Course;
import com.example.model.Lesson;
import com.example.model.Material;
import com.example.repository.CourseRepository;
import com.example.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class LessonServiceImpl implements LessonService {
    private final CourseRepository courseRepo;
    private final LessonRepository lessonRepo;

    public LessonServiceImpl(CourseRepository courseRepo, LessonRepository lessonRepo) {
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
    }

    @Override
    public List<Lesson> getLessonsForCourse(Integer courseId) {
        courseRepo.ensureCourseExists(courseId);

        return lessonRepo.findAllByCourseId(courseId);
    }


    @Override
    public void addLessonToCourse(Integer courseId, Lesson lesson) {
        courseRepo.ensureCourseExists(courseId);
        Objects.requireNonNull(lesson, "Lesson cannot be null");

        lesson.setCourseId(courseId);

        lessonRepo.save(lesson);
    }

    @Override
    public void removeLessonFromCourse(Integer courseId, Integer lessonId) {
        courseRepo.ensureCourseExists(courseId);
        lessonRepo.ensureLessonExists(lessonId);

        Course course = courseRepo.findById(courseId).get();

        boolean lessonBelongsToCourse = course.getLessons().stream()
                .anyMatch(lesson -> lesson.getId().equals(lessonId));

        if (!lessonBelongsToCourse) {
            throw new IllegalArgumentException("Lesson does not belong to the given course");
        }

        lessonRepo.delete(lessonId);
    }

    @Override
    public void addMaterialToLesson(Integer lessonId, Material material) {
        lessonRepo.ensureLessonExists(lessonId);
        Objects.requireNonNull(material, "Material cannot be null");

        lessonRepo.addMaterial(lessonId, material);
    }

    @Override
    public void removeMaterialFromLesson(Integer lessonId, Integer materialId) {
        lessonRepo.ensureLessonExists(lessonId);
        Objects.requireNonNull(materialId, "Material ID cannot be null");

        lessonRepo.removeMaterial(lessonId, materialId);
    }
}