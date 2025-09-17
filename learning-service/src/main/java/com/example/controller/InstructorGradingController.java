package com.example.controller;

import com.example.dto.grade.GradeSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.service.InstructorGradingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors/{instructorId}/grading")
public class InstructorGradingController {

    private final InstructorGradingService gradingService;

    public InstructorGradingController(InstructorGradingService gradingService) {
        this.gradingService = gradingService;
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<Void> gradeSubmission(@PathVariable Long instructorId,
                                                @PathVariable Long submissionId,
                                                @Valid @RequestBody GradeSubmissionRequest request) {
        gradingService.gradeSubmission(instructorId, submissionId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<Page<SubmissionResponse>> getSubmissionsForAssignment(
            @PathVariable Long instructorId,
            @PathVariable Long assignmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SubmissionResponse> submissions = gradingService.getSubmissionsForAssignment(instructorId, assignmentId, pageable);
        return ResponseEntity.ok(submissions);
    }
}