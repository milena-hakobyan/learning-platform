package com.example.service;

import com.example.dto.instructor.InstructorResponse;
import com.example.dto.instructor.RegisterInstructorRequest;
import com.example.dto.student.RegisterStudentRequest;
import com.example.dto.student.StudentResponse;
import com.example.model.Instructor;
import com.example.model.Student;

public interface RegistrationService {

    StudentResponse registerStudent(RegisterStudentRequest request);

    InstructorResponse registerInstructor(RegisterInstructorRequest request);

}
