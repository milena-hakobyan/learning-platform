package com.example.dto.course;

import com.example.annotation.AtLeastOneFieldPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@AtLeastOneFieldPresent(message = "At least one field must be provided for update")
@Data
@NoArgsConstructor
public class UpdateCourseRequest {

    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    private String description;

    private String category;

    @Pattern(
            regexp = "^(https?://).+",
            message = "URL must start with http:// or https://"
    )
    private String url;
}
