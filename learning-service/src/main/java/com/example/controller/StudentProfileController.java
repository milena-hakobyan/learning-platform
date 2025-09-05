package com.example.controller;

import com.example.dto.instructor.InstructorResponse;
import com.example.dto.student.StudentResponse;
import com.example.dto.student.UpdateStudentRequest;
import com.example.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<Page<StudentResponse>> getAllInstructors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentResponse> instructors = studentService.getAllStudents(pageable);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long studentId) {

        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long studentId,
            @Valid @RequestBody UpdateStudentRequest request) {

        return ResponseEntity.ok(studentService.updateStudent(studentId, request));
    }
}