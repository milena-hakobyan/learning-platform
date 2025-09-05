package com.example.mapper;

import com.example.dto.lesson.CreateLessonRequest;
import com.example.dto.lesson.LessonResponse;
import com.example.dto.lesson.UpdateLessonRequest;
import com.example.model.Course;
import com.example.model.Lesson;
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
        response.setId(lesson.getId());
        response.setTitle(lesson.getTitle());
        response.setContent(lesson.getContent());
        response.setUploadedAt(lesson.getUploadedAt());
        response.setCourseId(lesson.getCourse().getId());
        return response;
    }

    public Lesson toEntity(CreateLessonRequest dto, Course course) {
        Lesson lesson = new Lesson();
        lesson.setTitle(dto.getTitle());
        lesson.setContent(dto.getContent());
        lesson.setUploadedAt(dto.getUploadedAt());
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
    }
}
