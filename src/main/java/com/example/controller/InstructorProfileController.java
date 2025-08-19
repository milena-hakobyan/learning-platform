package com.example.controller;

import com.example.dto.instructor.InstructorResponse;
import com.example.dto.instructor.UpdateInstructorRequest;
import com.example.service.InstructorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorProfileController {

    private final InstructorProfileService profileService;

    @GetMapping
    public ResponseEntity<Page<InstructorResponse>> getAllInstructors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InstructorResponse> instructors = profileService.getAllInstructors(pageable);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/{instructorId}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Long instructorId) {
        return ResponseEntity.ok(profileService.getInstructorById(instructorId));
    }

    @PutMapping("/{instructorId}")
    public ResponseEntity<InstructorResponse> updateInstructor(
            @PathVariable Long instructorId,
            @RequestBody UpdateInstructorRequest request) {

        return ResponseEntity.ok(profileService.updateInstructor(instructorId, request));
    }
}
