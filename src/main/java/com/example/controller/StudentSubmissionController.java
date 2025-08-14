package com.example.controller;

import com.example.dto.submission.CreateSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students/{studentId}/submissions")
@RequiredArgsConstructor
public class StudentSubmissionController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<SubmissionResponse>> getSubmissions(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getSubmissionsByStudentId(studentId));
    }

    @PostMapping
    public ResponseEntity<Void> submitAssignment(
            @PathVariable Long studentId,
            @RequestBody CreateSubmissionRequest request) {

        if (studentService.hasSubmitted(studentId, request.getAssignmentId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        studentService.submitAssignment(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
