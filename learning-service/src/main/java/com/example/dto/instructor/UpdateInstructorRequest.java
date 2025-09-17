package com.example.dto.instructor;

import com.example.annotation.AtLeastOneFieldPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@AtLeastOneFieldPresent
@Data
@NoArgsConstructor
public class UpdateInstructorRequest {

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
}