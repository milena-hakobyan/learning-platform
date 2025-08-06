package com.example.dto.grade;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class GradeResponse {
    private Long id;
    private Long submissionId;
    private Double score;
    private String feedback;
    private LocalDateTime gradedAt;
}
