package com.example.controller;


import com.example.dto.lesson.LessonResponse;
import com.example.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/lessons")
    public ResponseEntity<Page<LessonResponse>> getLessonsForCourse(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LessonResponse> lessonsPage = lessonService.getLessonsForCourse(courseId, pageable);
        return ResponseEntity.ok(lessonsPage);
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonResponse> getLessonDetails(@PathVariable Long lessonId) {
        return ResponseEntity.ok(lessonService.getLessonById(lessonId));
    }

}
