package com.example.dto.student;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class StudentResponse {
    private Long userId;
    private Double progressPercentage;
    private Integer completedCourses;
    private Integer currentCourses;
    private List<Long> enrolledCourseIds;
}