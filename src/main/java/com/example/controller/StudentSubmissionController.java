package com.example.controller;

import com.example.dto.submission.CreateSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<SubmissionResponse>> getSubmissions(@PathVariable Long studentId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(studentService.getSubmissionsByStudentId(studentId, pageable));
    }

    @PostMapping
    public ResponseEntity<Void> submitAssignment(
            @PathVariable Long studentId,
            @Valid @RequestBody CreateSubmissionRequest request) {

        if (studentService.hasSubmitted(studentId, request.getAssignmentId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        studentService.submitAssignment(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
