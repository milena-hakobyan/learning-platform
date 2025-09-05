
package com.example.controller;

import com.example.dto.grade.GradeResponse;
import com.example.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/students/{studentId}/grades")
@RequiredArgsConstructor
public class StudentGradeController {

    private final StudentService studentService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<GradeResponse>> getGradesForCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(studentService.getGradesForCourse(courseId, studentId, pageable));
    }
    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<GradeResponse> getAssignmentGrade(@PathVariable Long studentId,
                                                            @PathVariable Long assignmentId) {
        return studentService.getAssignmentGradeForStudent(assignmentId, studentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

