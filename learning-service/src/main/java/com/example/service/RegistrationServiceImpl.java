package com.example.service;

import com.example.dto.instructor.InstructorResponse;
import com.example.dto.instructor.RegisterInstructorRequest;
import com.example.dto.student.RegisterStudentRequest;
import com.example.dto.student.StudentResponse;
import com.example.feign.UserServiceClient;
import com.example.mapper.InstructorMapper;
import com.example.mapper.StudentMapper;
import com.example.model.Instructor;
import com.example.model.Student;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaStudentRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserServiceClient userClient;
    private final JpaStudentRepository studentRepo;
    private final JpaInstructorRepository instructorRepo;
    private final StudentMapper studentMapper;
    private final InstructorMapper instructorMapper;

    public RegistrationServiceImpl(UserServiceClient userClient,
                                   JpaStudentRepository studentRepo,
                                   JpaInstructorRepository instructorRepo,
                                   StudentMapper studentMapper,
                                   InstructorMapper instructorMapper) {
        this.userClient = userClient;
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
        this.studentMapper = studentMapper;
        this.instructorMapper = instructorMapper;
    }

    @Override
    public StudentResponse registerStudent(RegisterStudentRequest request) {
        Long userId = userClient.registerStudent(request.toUserRequest());
        Student student = new Student(userId);
        return studentMapper.toDto(studentRepo.save(student));
    }

    @Override
    public InstructorResponse registerInstructor(RegisterInstructorRequest request) {
        Long userId = userClient.registerInstructor(request.toUserRequest());
        Instructor instructor = new Instructor(userId, request.getBio());
        return instructorMapper.toDto(instructorRepo.save(instructor));
    }
}
