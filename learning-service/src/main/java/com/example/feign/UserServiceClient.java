package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
        name = "user-service"
)
public interface UserServiceClient {

    @PostMapping("/api/users/register/student")
    Long registerStudent(@RequestBody Map<String, Object> request);

    @PostMapping("/api/users/register/instructor")
    Long registerInstructor(@RequestBody Map<String, Object> request);

    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);

    @PostMapping("/api/users/{id}/logs")
    void logActivity(@PathVariable("id") Long id, @RequestBody ActivityLogRequest request);

    record UserResponse(Long id, String username, String email, String role) {}
    record ActivityLogRequest(String action) {}
}
