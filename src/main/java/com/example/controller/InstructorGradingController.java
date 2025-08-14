package com.example.controller;

import com.example.dto.grade.GradeResponse;
import com.example.dto.grade.GradeSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.service.InstructorGradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors/{instructorId}")
@RequiredArgsConstructor
public class InstructorGradingController {

    private final InstructorGradingService gradingService;

    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsForAssignment(
            @PathVariable Long instructorId,
            @PathVariable Long assignmentId) {

        List<SubmissionResponse> submissions = gradingService.getSubmissionsForAssignment(instructorId, assignmentId);
        return ResponseEntity.ok(submissions);
    }

    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<GradeResponse> gradeSubmission(
            @PathVariable Long instructorId,
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeSubmissionRequest request) {

        GradeResponse grade = gradingService.gradeSubmission(instructorId, submissionId, request);
        return ResponseEntity.ok(grade);
    }

}
