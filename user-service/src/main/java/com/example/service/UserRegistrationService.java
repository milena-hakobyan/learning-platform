package com.example.service;

import com.example.dto.user.*;

public interface UserRegistrationService {
    Long registerStudent(RegisterStudentRequest request);
    Long registerInstructor(RegisterInstructorRequest request);
}
