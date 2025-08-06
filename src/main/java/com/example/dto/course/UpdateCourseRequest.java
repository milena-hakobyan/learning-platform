package com.example.dto.course;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCourseRequest {
    private String title;
    private String description;
    private String category;
    private String url;
}
