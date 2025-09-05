package com.example.controller;

import com.example.dto.material.MaterialResponse;
import com.example.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/{studentId}")
@RequiredArgsConstructor
public class StudentMaterialController {

    private final StudentService studentService;

    /**
     * Get all materials for a lesson, scoped to the student.
     */
    @GetMapping("/lessons/{lessonId}/materials")
    public ResponseEntity<List<MaterialResponse>> getLessonMaterials(
            @PathVariable Long studentId,
            @PathVariable Long lessonId) {

        List<MaterialResponse> materials = studentService.accessLessonMaterials(studentId, lessonId);
        return ResponseEntity.ok(materials);
    }

    /**
     * Get all materials for an assignment, scoped to the student.
     */
    @GetMapping("/assignments/{assignmentId}/materials")
    public ResponseEntity<List<MaterialResponse>> getAssignmentMaterials(
            @PathVariable Long studentId,
            @PathVariable Long assignmentId) {

        List<MaterialResponse> materials = studentService.accessAssignmentMaterials(studentId, assignmentId);
        return ResponseEntity.ok(materials);
    }
}
