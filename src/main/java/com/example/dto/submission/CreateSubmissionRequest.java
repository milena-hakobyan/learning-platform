package com.example.dto.submission;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateSubmissionRequest {

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotBlank(message = "Content link must not be blank")
    private String contentLink;
}