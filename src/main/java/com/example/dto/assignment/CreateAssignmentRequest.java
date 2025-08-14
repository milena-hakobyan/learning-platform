package com.example.dto.assignment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CreateAssignmentRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private double maxScore;
}
