package com.example.dto.instructor;

import com.example.dto.course.CourseResponse;
import com.example.dto.course.CourseSummaryResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class InstructorResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private int totalCoursesCreated;
    private double rating;
    private boolean isVerified;
    private List<CourseSummaryResponse> coursesCreated;
}