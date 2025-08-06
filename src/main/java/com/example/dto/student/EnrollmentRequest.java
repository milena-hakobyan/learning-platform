package com.example.dto.student;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnrollmentRequest {
    Long studentId;
    Long courseId;
}
