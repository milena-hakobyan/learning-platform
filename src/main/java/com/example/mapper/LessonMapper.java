package com.example.mapper;

import com.example.dto.lesson.CreateLessonRequest;
import com.example.dto.lesson.LessonResponse;
import com.example.dto.lesson.UpdateLessonRequest;
import com.example.dto.material.MaterialResponse;
import com.example.model.Course;
import com.example.model.Lesson;
import com.example.model.Material;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LessonMapper {

    private final MaterialMapper materialMapper;

    public LessonMapper(MaterialMapper materialMapper) {
        this.materialMapper = materialMapper;
    }

    public LessonResponse toDto(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setTitle(lesson.getTitle());
        response.setContent(lesson.getContent());
        response.setUploadDate(lesson.getUploadDate());
        response.setCourseId(lesson.getCourse().getId());
        response.setMaterials(
                lesson.getMaterials().stream()
                        .map(materialMapper::toDto)
                        .collect(Collectors.toList())
        );
        return response;
    }

    public Lesson toEntity(CreateLessonRequest dto, Course course) {
        Lesson lesson = new Lesson();
        lesson.setTitle(dto.getTitle());
        lesson.setContent(dto.getContent());
        lesson.setUploadDate(dto.getUploadDate());
        lesson.setCourse(course);
        lesson.setMaterials(List.of());
        return lesson;
    }

    public void updateEntity(UpdateLessonRequest dto, Lesson lesson) {
        if (dto.getTitle() != null) {
            lesson.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            lesson.setContent(dto.getContent());
        }
        if (dto.getUploadDate() != null) {
            lesson.setUploadDate(dto.getUploadDate());
        }
    }
}
