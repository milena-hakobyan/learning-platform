package com.example.dto.course;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String url;

    private String instructorName;

    private int lessonsCount;
    private int assignmentsCount;
    private int enrolledStudentCount;
}
