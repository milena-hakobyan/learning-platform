package com.example.service;

import com.example.dto.grade.GradeResponse;
import com.example.dto.grade.GradeSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.GradeMapper;
import com.example.mapper.SubmissionMapper;
import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorGradingServiceImpl implements InstructorGradingService {
    private final JpaInstructorRepository instructorRepo;
    private final JpaUserRepository userRepo;
    private final JpaAssignmentRepository assignmentRepo;
    private final JpaSubmissionRepository submissionRepo;
    private final JpaGradeRepository gradeRepo;
    private final JpaActivityLogRepository activityLogRepo;
    private final InstructorAuthorizationService instructorAuthorizationService;
    private final SubmissionMapper submissionMapper;
    private final GradeMapper gradeMapper;

    public InstructorGradingServiceImpl(JpaInstructorRepository instructorRepo, JpaUserRepository userRepo, JpaAssignmentRepository assignmentRepo, JpaSubmissionRepository submissionRepo, JpaGradeRepository gradeRepo, JpaActivityLogRepository activityLogRepo, InstructorAuthorizationService instructorAuthorizationService, SubmissionMapper submissionMapper, GradeMapper gradeMapper) {
        this.instructorRepo = instructorRepo;
        this.userRepo = userRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.gradeRepo = gradeRepo;
        this.activityLogRepo = activityLogRepo;
        this.instructorAuthorizationService = instructorAuthorizationService;
        this.submissionMapper = submissionMapper;
        this.gradeMapper = gradeMapper;
    }

    @Override
    @Transactional
    public GradeResponse gradeSubmission(Long instructorId, Long submissionId, GradeSubmissionRequest request) {
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

        User user = userRepo.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor with ID " + instructorId + " not found"));

        ActivityLog activityLog = new ActivityLog(user, "Graded submission ID: " + submission.getId());
        activityLogRepo.save(activityLog);

        return gradeMapper.toDto(grade);
    }




    @Override
    public List<SubmissionResponse> getSubmissionsForAssignment(Long instructorId, Long assignmentId) {
        Objects.requireNonNull(assignmentId, "Submission ID cannot be null");

        Assignment assignment = assignmentRepo.findById(assignmentId)
                        .orElseThrow(() -> new IllegalArgumentException("Assignment with ID " + assignmentId + " not found"));

        instructorAuthorizationService.ensureAuthorizedCourseAccess(instructorId, assignment.getCourse().getId());

        List<Submission> submissions = submissionRepo.findAllByAssignmentId(assignmentId);

        User user = userRepo.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Viewed submissions for assignment with id : " + assignmentId);
        activityLogRepo.save(activityLog);
        return submissions
                .stream()
                .map(submissionMapper::toDto)
                .toList();
    }

}
