
package com.example.controller;

import com.example.dto.grade.GradeResponse;
import com.example.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/students/{studentId}/grades")
@RequiredArgsConstructor
public class StudentGradeController {

    private final StudentService studentService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<GradeResponse>> getGradesForCourse(@PathVariable Long studentId,
                                                                  @PathVariable Long courseId) {
        return ResponseEntity.ok(studentService.getGradesForCourse(courseId, studentId));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<GradeResponse> getAssignmentGrade(@PathVariable Long studentId,
                                                            @PathVariable Long assignmentId) {
        return studentService.getAssignmentGradeForStudent(assignmentId, studentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

