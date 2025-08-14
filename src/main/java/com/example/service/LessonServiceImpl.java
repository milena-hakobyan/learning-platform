package com.example.service;

import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.assignment.UpdateAssignmentRequest;
import com.example.dto.lesson.CreateLessonRequest;
import com.example.dto.lesson.LessonResponse;
import com.example.dto.lesson.UpdateLessonRequest;
import com.example.dto.material.CreateMaterialRequest;
import com.example.dto.material.MaterialResponse;
import com.example.mapper.AssignmentMapper;
import com.example.mapper.LessonMapper;
import com.example.mapper.MaterialMapper;
import com.example.model.*;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaLessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class LessonServiceImpl implements LessonService {
    private final JpaCourseRepository courseRepo;
    private final JpaLessonRepository lessonRepo;
    private final JpaInstructorRepository instructorRepo;
    private final LessonMapper lessonMapper;
    private final MaterialMapper materialMapper;
    private final AssignmentMapper assignmentMapper;

    public LessonServiceImpl(JpaCourseRepository courseRepo, JpaLessonRepository lessonRepo, JpaInstructorRepository instructorRepo, LessonMapper lessonMapper, MaterialMapper materialMapper, AssignmentMapper assignmentMapper) {
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
        this.instructorRepo = instructorRepo;
        this.lessonMapper = lessonMapper;
        this.materialMapper = materialMapper;
        this.assignmentMapper = assignmentMapper;
    }

    @Override
    public List<LessonResponse> getLessonsForCourse(Long courseId) {
        courseRepo.existsById(courseId);

        return lessonRepo.findAllByCourseId(courseId)
                .stream()
                .map(lessonMapper::toDto)
                .toList();
    }


    @Override
    public LessonResponse addLessonToCourse(Long courseId, CreateLessonRequest request) {
        Objects.requireNonNull(request, "CreateLessonRequest cannot be null");

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + courseId + " not found"));

        Lesson lesson = lessonMapper.toEntity(request, course);

        Lesson savedLesson = lessonRepo.save(lesson);

        return lessonMapper.toDto(savedLesson);
    }


    @Override
    public LessonResponse updateLesson(Long lessonId, UpdateLessonRequest dto) {
        Objects.requireNonNull(lessonId, "Lesson ID cannot be null");
        Objects.requireNonNull(dto, "Lesson request cannot be null");

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson with ID " + lessonId + " not found"));

        lessonMapper.updateEntity(dto, lesson);
        Lesson saved = lessonRepo.save(lesson);

        return lessonMapper.toDto(saved);
    }


    @Override
    public void removeLessonFromCourse(Long courseId, Long lessonId) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(lessonId, "Lesson ID cannot be null");

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Lesson does not belong to the given course");
        }

        lessonRepo.deleteById(lessonId);
    }


    @Override
    public MaterialResponse addMaterialToLesson(Long lessonId, CreateMaterialRequest request) {
        Objects.requireNonNull(request, "Material Create Request cannot be null");

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        Instructor instructor = instructorRepo.findById(request.getInstructorId())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        Material material = materialMapper.toEntity(request, instructor);

        lesson.addMaterial(material);
        lessonRepo.save(lesson);

        return materialMapper.toDto(material);
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

    @Override
    public LessonResponse getLessonById(Long lessonId) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with id: " + lessonId));
        return lessonMapper.toDto(lesson);
    }
}