package com.example.dto.instructor;

import com.example.dto.course.CourseResponse;
import com.example.dto.course.CourseSummaryResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class InstructorResponse {
    private Long userId;
    private String bio;
    private int totalCoursesCreated;
    private double rating;
    private boolean isVerified;
    private List<CourseSummaryResponse> coursesCreated;
}