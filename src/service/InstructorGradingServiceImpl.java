package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorGradingServiceImpl implements InstructorGradingService {
    private final InstructorRepository instructorRepo;
    private final UserRepository userRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;
    private final GradeRepository gradeRepo;
    private final ActivityLogRepository activityLogRepo;

    public InstructorGradingServiceImpl(InstructorRepository instructorRepo, UserRepository userRepo, AssignmentRepository assignmentRepo, SubmissionRepository submissionRepo, GradeRepository gradeRepo, ActivityLogRepository activityLogRepo) {
        this.instructorRepo = instructorRepo;
        this.userRepo= userRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.gradeRepo = gradeRepo;
        this.activityLogRepo = activityLogRepo;
    }


    @Override
    public void gradeSubmission(Long instructorId, Long submissionId, Grade grade) {
        Objects.requireNonNull(grade, "Grade cannot be null");
        instructorRepo.ensureInstructorExists(instructorId);
        submissionRepo.ensureSubmissionExists(submissionId);

        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() ->  new IllegalArgumentException("Submission not found"));

        submission.setStatus(SubmissionStatus.GRADED);
        gradeRepo.save(grade);

        submissionRepo.update(submission);

        User user = userRepo.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUser(user);
        activityLog.setAction("Graded submission ID: " + submission.getId());
        activityLogRepo.save(activityLog);
    }

    @Override
    public java.util.List<Submission> getSubmissionsForAssignment(Long instructorId, Long assignmentId) {
        instructorRepo.ensureInstructorExists(instructorId);
        assignmentRepo.ensureAssignmentExists(assignmentId);

        List<Submission> submissions = submissionRepo.findAllByAssignmentId(assignmentId);


        User user = userRepo.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Viewed submissions for assignment with id : " + assignmentId);
        activityLogRepo.save(activityLog);

        return submissions;
    }

}
