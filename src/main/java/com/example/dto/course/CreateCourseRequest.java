package com.example.dto.course;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCourseRequest {
    private String title;
    private String description;
    private String category;
    private String url;
    private Long instructorId;
}