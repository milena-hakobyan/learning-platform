package com.example.dto.student;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterStudentRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
