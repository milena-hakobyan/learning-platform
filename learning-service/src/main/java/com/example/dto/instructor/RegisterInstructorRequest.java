package com.example.dto.instructor;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RegisterInstructorRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String bio;

    public Map<String, Object> toUserRequest() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("email", email);
        map.put("password", password);
        return map;
    }
}
