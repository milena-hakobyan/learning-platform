package com.example.controller;

import com.example.dto.instructor.InstructorResponse;
import com.example.dto.student.StudentResponse;
import com.example.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/student")
    public ResponseEntity<StudentResponse> registerStudent(@RequestParam String username,
                                                           @RequestParam String firstName,
                                                           @RequestParam String lastName,
                                                           @RequestParam String email,
                                                           @RequestParam String password) {
        StudentResponse response = registrationService.registerStudent(username, firstName, lastName, email, password);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/instructor")
    public ResponseEntity<InstructorResponse> registerInstructor(@RequestParam String username,
                                                                 @RequestParam String firstName,
                                                                 @RequestParam String lastName,
                                                                 @RequestParam String email,
                                                                 @RequestParam String password,
                                                                 @RequestParam String bio) {
        InstructorResponse response = registrationService.registerInstructor(username, firstName, lastName, email, password, bio);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
