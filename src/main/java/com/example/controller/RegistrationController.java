package com.example.controller;

import com.example.dto.instructor.InstructorResponse;
import com.example.dto.instructor.RegisterInstructorRequest;
import com.example.dto.student.RegisterStudentRequest;
import com.example.dto.student.StudentResponse;
import com.example.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/student")
    public ResponseEntity<StudentResponse> registerStudent(@Valid @RequestBody RegisterStudentRequest request) {
        StudentResponse response = registrationService.registerStudent(request.getUsername(), request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/instructor")
    public ResponseEntity<InstructorResponse> registerInstructor(@Valid @RequestBody RegisterInstructorRequest request) {
        InstructorResponse response = registrationService.registerInstructor(request.getUsername(), request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword(), request.getBio());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
