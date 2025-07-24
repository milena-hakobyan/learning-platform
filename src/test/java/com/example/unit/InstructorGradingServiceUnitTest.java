package com.example.unit;

import com.example.model.*;
import com.example.repository.*;
import com.example.service.InstructorGradingService;
import com.example.service.InstructorGradingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorGradingServiceUnitTest {

    @Mock
    private InstructorRepository instructorRepo;
    @Mock
    private AssignmentRepository assignmentRepo;
    @Mock
    private SubmissionRepository submissionRepo;
    @Mock
    private GradeRepository gradeRepo;
    @Mock
    private ActivityLogRepository activityLogRepo;

    @InjectMocks
    private InstructorGradingServiceImpl gradingService;

    @Test
    void gradeSubmission_shouldThrow_whenGradeIsNull() {
        Integer instructorId = 1;
        Integer submissionId = 10;

        assertThrows(NullPointerException.class, () ->
                gradingService.gradeSubmission(instructorId, submissionId, null));

        verifyNoInteractions(gradeRepo);
        verifyNoInteractions(submissionRepo);
        verifyNoInteractions(activityLogRepo);
    }

    @Test
    void gradeSubmission_shouldThrow_whenSubmissionNotFound() {
        Integer instructorId = 1;
        Integer submissionId = 10;
        Grade grade = new Grade(1, 90.0, submissionId, "Well done", LocalDateTime.now());

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        doNothing().when(submissionRepo).ensureSubmissionExists(submissionId);
        when(submissionRepo.findById(submissionId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                gradingService.gradeSubmission(instructorId, submissionId, grade));

        verify(submissionRepo, never()).update(any());
        verify(gradeRepo, never()).save(any());
    }

    @Test
    void gradeSubmission_shouldGrade_whenValid() {
        Integer instructorId = 1;
        Integer submissionId = 10;
        Grade grade = new Grade(1, 90.0, submissionId, "Well done", LocalDateTime.now());
        Submission submission = new Submission(submissionId, 2, 3, "link", LocalDateTime.now());

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        doNothing().when(submissionRepo).ensureSubmissionExists(submissionId);
        when(submissionRepo.findById(submissionId)).thenReturn(Optional.of(submission));

        gradingService.gradeSubmission(instructorId, submissionId, grade);

        assertEquals(SubmissionStatus.GRADED, submission.getStatus());
        verify(gradeRepo).save(grade);
        verify(submissionRepo).update(submission);
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(instructorId) &&
                        log.getAction().contains("Graded submission ID: " + submissionId)
        ));
    }

    @Test
    void getSubmissionsForAssignment_shouldReturnListAndLogActivity() {
        Integer instructorId = 1;
        Integer assignmentId = 5;
        List<Submission> submissions = List.of(
                new Submission(1, assignmentId, "link", LocalDateTime.now())
        );

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        doNothing().when(assignmentRepo).ensureAssignmentExists(assignmentId);
        when(submissionRepo.findAllByAssignmentId(assignmentId)).thenReturn(submissions);

        List<Submission> result = gradingService.getSubmissionsForAssignment(instructorId, assignmentId);

        assertEquals(submissions, result);
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(instructorId) &&
                        log.getAction().contains("Viewed submissions for assignment with id : " + assignmentId)
        ));
    }
}
