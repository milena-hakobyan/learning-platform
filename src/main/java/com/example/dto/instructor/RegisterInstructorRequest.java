package com.example.dto.instructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterInstructorRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String bio;
}
