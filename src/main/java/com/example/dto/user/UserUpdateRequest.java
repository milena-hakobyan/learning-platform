package com.example.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
