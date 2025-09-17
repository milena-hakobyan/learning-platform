package com.example.service;

import com.example.dto.grade.GradeSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.feign.UserServiceClient;
import com.example.mapper.GradeMapper;
import com.example.mapper.SubmissionMapper;
import com.example.model.*;
import com.example.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import feign.FeignException;

@Service
public class InstructorGradingServiceImpl implements InstructorGradingService {
    private final JpaAssignmentRepository assignmentRepo;
    private final JpaSubmissionRepository submissionRepo;
    private final JpaGradeRepository gradeRepo;
    private final InstructorAuthorizationService instructorAuthorizationService;
    private final SubmissionMapper submissionMapper;
    private final GradeMapper gradeMapper;
    private final UserServiceClient userClient;

    public InstructorGradingServiceImpl(
            JpaAssignmentRepository assignmentRepo,
            JpaSubmissionRepository submissionRepo,
            JpaGradeRepository gradeRepo,
            InstructorAuthorizationService instructorAuthorizationService,
            SubmissionMapper submissionMapper,
            GradeMapper gradeMapper,
            UserServiceClient userClient
    ) {
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.gradeRepo = gradeRepo;
        this.instructorAuthorizationService = instructorAuthorizationService;
        this.submissionMapper = submissionMapper;
        this.gradeMapper = gradeMapper;
        this.userClient = userClient;
    }

    @Override
    @Transactional
    public void gradeSubmission(Long instructorId, Long submissionId, GradeSubmissionRequest request) {
        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission with ID " + submissionId + " not found"));

        instructorAuthorizationService.ensureAuthorizedCourseAccess(
                instructorId, submission.getAssignment().getCourse().getId());

        if (submission.getStatus() == SubmissionStatus.GRADED) {
            throw new IllegalStateException("Submission already graded");
        }

        Grade grade = gradeMapper.toEntity(request, submission);
        gradeRepo.save(grade);

        submission.setStatus(SubmissionStatus.GRADED);
        submissionRepo.save(submission);

        logActivity(instructorId, "Graded submission ID: " + submission.getId());
    }

    @Override
    public Page<SubmissionResponse> getSubmissionsForAssignment(Long instructorId, Long assignmentId, Pageable pageable) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment with ID " + assignmentId + " not found"));

        instructorAuthorizationService.ensureAuthorizedCourseAccess(instructorId, assignment.getCourse().getId());

        Page<Submission> submissions = submissionRepo.findAllByAssignmentId(assignmentId, pageable);

        logActivity(instructorId, "Viewed submissions for assignment with ID: " + assignmentId);

        return submissions.map(submissionMapper::toDto);
    }

    /**
     * Logs activity via the User service.
     * Throws ResourceNotFoundException if user does not exist.
     */
    private void logActivity(Long userId, String action) {
        try {
            userClient.logActivity(userId, new UserServiceClient.ActivityLogRequest(action));
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }
}
