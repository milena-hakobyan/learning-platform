package com.example.dto.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateMaterialRequest {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Content type must not be blank")
    private String contentType;

    @NotBlank(message = "Category must not be blank")
    private String category;

    @NotBlank(message = "URL must not be blank")
    @Pattern(
            regexp = "^(https?://).+$",
            message = "URL must be a valid HTTP or HTTPS URL"
    )
    private String url;

    @NotNull(message = "Instructor ID must be provided")
    @Positive(message = "Instructor ID must be positive")
    private Long instructorId;
}