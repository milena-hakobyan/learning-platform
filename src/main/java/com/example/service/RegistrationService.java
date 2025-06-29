package com.example.service;

import com.example.model.Instructor;
import com.example.model.Student;

public interface RegistrationService {

    Student registerStudent(String username, String firstName, String lastName, String email, String rawPassword);

    Instructor registerInstructor(String username, String firstName, String lastName, String email, String rawPassword, String bio);

}
