package com.example.dto.material;

import com.example.annotation.AtLeastOneFieldPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@AtLeastOneFieldPresent(message = "At least one field must be provided for update")
@Data
@NoArgsConstructor
public class UpdateMaterialRequest {

    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    private String contentType;

    private String category;

    @Pattern(regexp = "^(https?://).*$", message = "URL must be a valid HTTP or HTTPS URL")
    private String url;
}