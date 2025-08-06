package com.example.dto.grade;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GradeSubmissionRequest {
    private Double score;
    private String feedback;
}
