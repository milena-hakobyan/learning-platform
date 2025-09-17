package com.example.dto.lesson;

import com.example.annotation.AtLeastOneFieldPresent;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AtLeastOneFieldPresent(message = "At least one field must be provided for update")
@Data
@NoArgsConstructor
public class UpdateLessonRequest {

    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @Size(max = 5000, message = "Content must be at most 5000 characters")
    private String content;
}