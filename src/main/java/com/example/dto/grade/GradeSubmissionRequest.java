package com.example.dto.grade;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GradeSubmissionRequest {

    @DecimalMin(value = "0.0", inclusive = true, message = "Score must be at least 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Score cannot exceed 100")
    private Double score;

    @Size(max = 500, message = "Feedback must not exceed 500 characters")
    private String feedback;
}