package com.example.controller;

import com.example.dto.student.StudentResponse;
import com.example.service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<StudentResponse>> getEnrolledStudents(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentResponse> studentsPage = enrollmentService.getEnrolledStudents(courseId, pageable);
        return ResponseEntity.ok(studentsPage);
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
