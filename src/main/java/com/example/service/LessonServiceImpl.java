package com.example.service;

import com.example.model.Course;
import com.example.model.Lesson;
import com.example.model.Material;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaLessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class LessonServiceImpl implements LessonService {
    private final JpaCourseRepository courseRepo;
    private final JpaLessonRepository lessonRepo;

    public LessonServiceImpl(JpaCourseRepository courseRepo, JpaLessonRepository lessonRepo) {
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
    }

    @Override
    public List<Lesson> getLessonsForCourse(Long courseId) {
        courseRepo.existsById(courseId);

        return lessonRepo.findAllByCourseId(courseId);
    }


    @Override
    public void addLessonToCourse(Long courseId, Lesson lesson) {
        Objects.requireNonNull(lesson, "Lesson cannot be null");
        courseRepo.existsById(courseId);

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        lesson.setCourse(course);

        lessonRepo.save(lesson);
    }

    @Override
    public void removeLessonFromCourse(Long courseId, Long lessonId) {
        courseRepo.existsById(courseId);
        lessonRepo.existsById(lessonId);

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Lesson does not belong to the given course");
        }

        lessonRepo.deleteById(lessonId);
    }


    @Override
    public void addMaterialToLesson(Long lessonId, Material material) {
        Objects.requireNonNull(material, "Material cannot be null");

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        lesson.addMaterial(material);
        lessonRepo.save(lesson);
    }

    @Override
    public void removeMaterialFromLesson(Long lessonId, Long materialId) {
        Objects.requireNonNull(materialId, "Material ID cannot be null");

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        Material material = lesson.getMaterials().stream()
                .filter(m -> m.getId().equals(materialId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Material not found in lesson"));

        lesson.removeMaterial(material);
        lessonRepo.save(lesson);
    }
}