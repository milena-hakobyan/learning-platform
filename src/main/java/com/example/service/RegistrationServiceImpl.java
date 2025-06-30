package com.example.service;

import com.example.model.Instructor;
import com.example.model.Student;
import com.example.model.User;
import com.example.repository.InstructorRepository;
import com.example.repository.StudentRepository;
import com.example.repository.UserRepository;
import com.example.utils.StringUtils;

import java.time.LocalDateTime;

public class RegistrationServiceImpl implements RegistrationService{
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

        Student student = new Student(username, firstName, lastName, email, StringUtils.applySha256(rawPassword),
                LocalDateTime.now());

        saveUser(student);
        return student;
    }

    @Override
    public Instructor registerInstructor(String username, String firstName, String lastName, String email, String rawPassword, String bio) {
        userRepo.ensureEmailAndUsernameAvailable(username, email);

        Instructor instructor = new Instructor(username, firstName, lastName, email, StringUtils.applySha256(rawPassword),
                LocalDateTime.now(), bio);

        saveUser(instructor);
        return instructor;
    }

    public void saveUser(User user) {
        userRepo.save(user);
        if (user instanceof Student) {
            studentRepo.save((Student) user);
        } else if (user instanceof Instructor) {
            instructorRepo.save((Instructor) user);
        }
    }
}
