package com.example.dto.submission;

import com.example.model.SubmissionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SubmissionResponse {
    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private String contentLink;
    private LocalDateTime submittedAt;
    private SubmissionStatus status;
}
