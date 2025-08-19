package com.example.dto.assignment;

import com.example.annotation.AtLeastOneFieldPresent;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AtLeastOneFieldPresent
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssignmentRequest {

    @Size(max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    @FutureOrPresent
    private LocalDateTime dueDate;

    @Positive
    private Double maxScore;
}
