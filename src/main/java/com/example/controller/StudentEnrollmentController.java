package com.example.controller;

import com.example.dto.course.CourseResponse;
import com.example.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Student self-service for course enrollment.
 */
@RestController
@RequestMapping("/api/students/{studentId}/courses")
@RequiredArgsConstructor
public class StudentEnrollmentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getEnrolledCourses(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getEnrolledCourses(studentId));
    }

    @PostMapping("/{courseId}")
    public ResponseEntity<Void> enrollInCourse(@PathVariable Long studentId,
                                               @PathVariable Long courseId) {
        studentService.enrollInCourse(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> dropCourse(@PathVariable Long studentId,
                                           @PathVariable Long courseId) {
        studentService.dropCourse(studentId, courseId);
        return ResponseEntity.noContent().build();
    }
}
