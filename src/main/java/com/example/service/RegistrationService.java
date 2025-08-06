package com.example.service;

import com.example.dto.instructor.InstructorResponse;
import com.example.dto.student.StudentResponse;
import com.example.model.Instructor;
import com.example.model.Student;

public interface RegistrationService {

    StudentResponse registerStudent(String username, String firstName, String lastName, String email, String rawPassword);

    InstructorResponse registerInstructor(String username, String firstName, String lastName, String email, String rawPassword, String bio);

}
