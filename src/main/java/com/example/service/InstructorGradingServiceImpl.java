package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

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

    public InstructorGradingServiceImpl(JpaInstructorRepository instructorRepo, JpaUserRepository userRepo, JpaAssignmentRepository assignmentRepo, JpaSubmissionRepository submissionRepo, JpaGradeRepository gradeRepo, JpaActivityLogRepository activityLogRepo, InstructorAuthorizationService instructorAuthorizationService) {
        this.instructorRepo = instructorRepo;
        this.userRepo = userRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.gradeRepo = gradeRepo;
        this.activityLogRepo = activityLogRepo;
        this.instructorAuthorizationService = instructorAuthorizationService;
    }

    @Override
    public void gradeSubmission(Long instructorId, Long submissionId, Grade grade) {
        Objects.requireNonNull(grade, "Grade cannot be null");
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        Objects.requireNonNull(submissionId, "Submission ID cannot be null");

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Invalid Instructor ID");
        }

        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        submission.setStatus(SubmissionStatus.GRADED);
        gradeRepo.save(grade);

        submissionRepo.save(submission);

        User user = userRepo.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Graded submission ID: " + submission.getId());
        activityLogRepo.save(activityLog);
    }


    @Override
    public List<Submission> getSubmissionsForAssignment(Long instructorId, Long assignmentId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        Objects.requireNonNull(assignmentId, "Submission ID cannot be null");

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Invalid Instructor ID");
        }

        if (!assignmentRepo.existsById(assignmentId)) {
            throw new IllegalArgumentException("Invalid Assignment ID");
        }

        List<Submission> submissions = submissionRepo.findAllByAssignmentId(assignmentId);


        User user = userRepo.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Viewed submissions for assignment with id : " + assignmentId);
        activityLogRepo.save(activityLog);
        return submissions;
    }

}
