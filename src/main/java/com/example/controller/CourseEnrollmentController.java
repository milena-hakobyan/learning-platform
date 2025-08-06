package com.example.controller;

import com.example.dto.student.StudentResponse;
import com.example.service.CourseEnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class CourseEnrollmentController {
    private final CourseEnrollmentService enrollmentService;

    public CourseEnrollmentController(CourseEnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/{courseId}/students")
    public ResponseEntity<List<StudentResponse>> getEnrolledStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrolledStudents(courseId));
    }

    @PostMapping("/{courseId}/students/{studentId}")
    public ResponseEntity<Void> enrollStudent(@PathVariable Long courseId,
                                              @PathVariable Long studentId) {
        enrollmentService.enrollStudent(courseId, studentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    public ResponseEntity<Void> unenrollStudent(@PathVariable Long courseId,
                                                @PathVariable Long studentId) {
        enrollmentService.unenrollStudent(courseId, studentId);
        return ResponseEntity.noContent().build();
    }
}
