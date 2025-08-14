package com.example.dto.lesson;

import com.example.dto.material.MaterialResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class LessonResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime uploadedAt;
    private Long courseId;
}
