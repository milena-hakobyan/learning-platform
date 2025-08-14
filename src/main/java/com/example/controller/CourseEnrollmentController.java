package com.example.controller;

import com.example.dto.student.StudentResponse;
import com.example.service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Instructor/Admin actions for managing course enrollments.
 */
@RestController
@RequestMapping("/api/courses/{courseId}/students")
@RequiredArgsConstructor
public class CourseEnrollmentController {

    private final CourseEnrollmentService enrollmentService;

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getEnrolledStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrolledStudents(courseId));
    }

    // instructor enrolls a student
    @PostMapping("/{studentId}")
    public ResponseEntity<Void> enrollStudent(@PathVariable Long courseId,
                                              @PathVariable Long studentId) {
        enrollmentService.enrollStudent(courseId, studentId);
        return ResponseEntity.noContent().build();
    }

    // instructor unenrolls a student
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> unenrollStudent(@PathVariable Long courseId,
                                                @PathVariable Long studentId) {
        enrollmentService.unenrollStudent(courseId, studentId);
        return ResponseEntity.noContent().build();
    }
}
