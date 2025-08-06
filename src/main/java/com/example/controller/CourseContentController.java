package com.example.controller;

import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.lesson.LessonResponse;
import com.example.service.AssignmentService;
import com.example.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/content")
public class CourseContentController {

    private final LessonService lessonService;
    private final AssignmentService assignmentService;

    public CourseContentController(LessonService lessonService, AssignmentService assignmentService) {
        this.lessonService = lessonService;
        this.assignmentService = assignmentService;
    }

    @GetMapping("/lessons")
    public ResponseEntity<List<LessonResponse>> getLessonsForCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsForCourse(courseId));
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsForCourse(courseId));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonResponse> getLessonDetails(@PathVariable Long lessonId) {
        return ResponseEntity.ok(lessonService.getLessonById(lessonId));
    }

    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignmentDetails(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(assignmentId));
    }

}
