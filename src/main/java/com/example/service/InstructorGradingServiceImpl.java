package com.example.service;

import com.example.model.ActivityLog;
import com.example.model.Grade;
import com.example.model.Submission;
import com.example.model.SubmissionStatus;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorGradingServiceImpl implements InstructorGradingService {
    private final InstructorRepository instructorRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;
    private final GradeRepository gradeRepo;
    private final ActivityLogRepository activityLogRepo;

    public InstructorGradingServiceImpl(InstructorRepository instructorRepo, AssignmentRepository assignmentRepo, SubmissionRepository submissionRepo, GradeRepository gradeRepo, ActivityLogRepository activityLogRepo) {
        this.instructorRepo = instructorRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.gradeRepo = gradeRepo;
        this.activityLogRepo = activityLogRepo;
    }


    @Override
    public void gradeSubmission(Integer instructorId, Integer submissionId, Grade grade) {
        instructorRepo.ensureInstructorExists(instructorId);
        submissionRepo.ensureSubmissionExists(submissionId);
        Objects.requireNonNull(grade, "Grade cannot be null");

        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() ->  new IllegalArgumentException("Submission not found"));

        submission.setStatus(SubmissionStatus.GRADED);
        gradeRepo.save(grade);
        submissionRepo.update(submission);
        activityLogRepo.save(new ActivityLog(instructorId, "Graded submission ID: " + submission.getSubmissionId()));
    }

    @Override
    public java.util.List<Submission> getSubmissionsForAssignment(Integer instructorId, Integer assignmentId) {
        instructorRepo.ensureInstructorExists(instructorId);
        assignmentRepo.ensureAssignmentExists(assignmentId);

        List<Submission> submissions = submissionRepo.findAllByAssignmentId(assignmentId);

        activityLogRepo.save(new ActivityLog(instructorId, "Viewed submissions for assignment with id : " + assignmentId));
        return submissions;
    }

}
