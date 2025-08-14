package com.example.dto.lesson;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
public class UpdateLessonRequest {
    private String title;
    private String content;
    private LocalDateTime uploadedAt;
}
