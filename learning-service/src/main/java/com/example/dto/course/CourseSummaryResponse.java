package com.example.dto.course;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CourseSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String instructorName;
}