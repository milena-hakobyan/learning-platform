package com.example.controller;


import com.example.dto.assignment.AssignmentResponse;
import com.example.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;


    @GetMapping("/assignments")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsForCourse(courseId));
    }


    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignmentDetails(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(assignmentId));
    }
}
