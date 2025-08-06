package com.example.dto.submission;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateSubmissionRequest {
    private Long assignmentId;
    private String contentLink;
}
