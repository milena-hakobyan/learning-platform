package com.example.controller;

import com.example.dto.instructor.InstructorResponse;
import com.example.service.InstructorProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
public class InstructorProfileController {

    private final InstructorProfileService profileService;

    public InstructorProfileController(InstructorProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<List<InstructorResponse>> getAllInstructors() {
        List<InstructorResponse> instructors = profileService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/{instructorId}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Long instructorId) {
        return profileService.getInstructorById(instructorId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
