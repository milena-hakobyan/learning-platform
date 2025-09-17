package com.example.mapper;

import com.example.dto.submission.CreateSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.model.Assignment;
import com.example.model.Student;
import com.example.model.Submission;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SubmissionMapper {

    public Submission toEntity(CreateSubmissionRequest dto, Student student, Assignment assignment) {
        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContentLink(dto.getContentLink());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(com.example.model.SubmissionStatus.SUBMITTED);
        return submission;
    }

    public SubmissionResponse toDto(Submission submission) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setAssignmentId(submission.getAssignment().getId());
        response.setAssignmentTitle(submission.getAssignment().getTitle());
        response.setStudentId(submission.getStudent().getUserId());
        response.setContentLink(submission.getContentLink());
        response.setSubmittedAt(submission.getSubmittedAt());
        response.setStatus(submission.getStatus());
        return response;
    }
}
