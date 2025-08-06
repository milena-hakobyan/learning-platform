package com.example.dto.student;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateStudentRequest {
    private Double progressPercentage;
    private Integer completedCourses;
    private Integer currentCourses;
}