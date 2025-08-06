package com.example.controller;

import com.example.dto.grade.GradeSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.service.InstructorGradingService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsForAssignment(@PathVariable Long instructorId,
                                                                                @PathVariable Long assignmentId) {
        List<SubmissionResponse> submissions = gradingService.getSubmissionsForAssignment(instructorId, assignmentId);
        return ResponseEntity.ok(submissions);
    }
}
