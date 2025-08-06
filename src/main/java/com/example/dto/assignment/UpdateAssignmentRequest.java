package com.example.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssignmentRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private double maxScore;
}
