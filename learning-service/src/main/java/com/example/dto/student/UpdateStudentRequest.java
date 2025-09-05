package com.example.dto.student;

import com.example.annotation.AtLeastOneFieldPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@AtLeastOneFieldPresent(message = "At least one field must be provided for update")
@Data
@NoArgsConstructor
public class UpdateStudentRequest {

    @Min(value = 0, message = "Progress percentage cannot be less than 0")
    @Max(value = 100, message = "Progress percentage cannot be more than 100")
    private Double progressPercentage;

    @PositiveOrZero(message = "Completed courses must be zero or positive")
    private Integer completedCourses;

    @PositiveOrZero(message = "Current courses must be zero or positive")
    private Integer currentCourses;
}