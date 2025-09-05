package com.example.dto.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateLessonRequest {

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Content must not be blank")
    private String content;

    private LocalDateTime uploadedAt;

    @NotNull(message = "Course ID must be provided")
    @Positive(message = "Course ID must be positive")
    private Long courseId;

    @Size(min = 1, message = "Material IDs list cannot be empty")
    private List<@Positive(message = "Material ID must be positive") Long> materialIds;
}
