package com.example.service;

import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.assignment.UpdateAssignmentRequest;
import com.example.dto.lesson.CreateLessonRequest;
import com.example.dto.lesson.LessonResponse;
import com.example.dto.lesson.UpdateLessonRequest;
import com.example.dto.material.CreateMaterialRequest;
import com.example.dto.material.MaterialResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.AssignmentMapper;
import com.example.mapper.LessonMapper;
import com.example.mapper.MaterialMapper;
import com.example.model.*;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaLessonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<LessonResponse> getLessonsForCourse(Long courseId, Pageable pageable) {
        courseRepo.existsById(courseId);

        return lessonRepo.findAllByCourseId(courseId, pageable)
                .map(lessonMapper::toDto);
    }


    @Override
    public LessonResponse addLessonToCourse(Long courseId, CreateLessonRequest request) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course with ID " + courseId + " not found"));

        Lesson lesson = lessonMapper.toEntity(request, course);

        Lesson savedLesson = lessonRepo.save(lesson);

        return lessonMapper.toDto(savedLesson);
    }


    @Override
    public LessonResponse updateLesson(Long lessonId, UpdateLessonRequest dto) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson with ID " + lessonId + " not found"));

        lessonMapper.updateEntity(dto, lesson);
        Lesson saved = lessonRepo.save(lesson);

        return lessonMapper.toDto(saved);
    }


    @Override
    public void removeLessonFromCourse(Long courseId, Long lessonId) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Lesson does not belong to the given course");
        }

        lessonRepo.deleteById(lessonId);
    }


    @Override
    public MaterialResponse addMaterialToLesson(Long lessonId, CreateMaterialRequest request) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        Instructor instructor = instructorRepo.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        Material material = materialMapper.toEntity(request, instructor);

        lesson.addMaterial(material);
        lessonRepo.save(lesson);

        return materialMapper.toDto(material);
    }

    @Override
    public void removeMaterialFromLesson(Long lessonId, Long materialId) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        Material material = lesson.getMaterials().stream()
                .filter(m -> m.getId().equals(materialId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Material not found in lesson"));

        lesson.removeMaterial(material);
        lessonRepo.save(lesson);
    }

    @Override
    public LessonResponse getLessonById(Long lessonId) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
        return lessonMapper.toDto(lesson);
    }
}