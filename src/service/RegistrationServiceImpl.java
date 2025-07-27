package com.example.service;

import com.example.model.Instructor;
import com.example.model.Role;
import com.example.model.Student;
import com.example.model.User;
import com.example.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final InstructorRepository instructorRepo;

    public RegistrationServiceImpl(UserRepository userRepo, StudentRepository studentRepo, InstructorRepository instructorRepo) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
    }

    @Override
    public Student registerStudent(String username, String firstName, String lastName, String email, String rawPassword) {
        userRepo.ensureEmailAndUsernameAvailable(username, email);

        User user = new User(username, firstName, lastName, email,
                StringUtils.applySha256(rawPassword),
                Role.STUDENT,
                LocalDateTime.now());
        user = userRepo.save(user);

        Student student = new Student(user);

        return studentRepo.save(student);
    }

    @Override
    public Instructor registerInstructor(String username, String firstName, String lastName, String email, String rawPassword, String bio) {
        userRepo.ensureEmailAndUsernameAvailable(username, email);

        User user = new User(username, firstName, lastName, email,
                StringUtils.applySha256(rawPassword),
                Role.INSTRUCTOR,
                LocalDateTime.now());
        user.setActive(true);
        user = userRepo.save(user);

        Instructor instructor = new Instructor(user, bio);

        return instructorRepo.save(instructor);
    }
}