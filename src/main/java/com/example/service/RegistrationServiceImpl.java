package com.example.service;

import com.example.model.Instructor;
import com.example.model.Student;
import com.example.model.User;
import com.example.repository.InstructorRepository;
import com.example.repository.StudentRepository;
import com.example.repository.UserRepository;
import com.example.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
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

        return (Student) saveUser(student);
    }

    @Override
    public Instructor registerInstructor(String username, String firstName, String lastName, String email, String rawPassword, String bio) {
        userRepo.ensureEmailAndUsernameAvailable(username, email);

        Instructor instructor = new Instructor(username, firstName, lastName, email, StringUtils.applySha256(rawPassword),
                LocalDateTime.now(), bio);

        return (Instructor) saveUser(instructor);
    }

    public User saveUser(User user) {
        if (user instanceof Student) {
            user = studentRepo.save((Student) user);
        } else if (user instanceof Instructor) {
            user = instructorRepo.save((Instructor) user);
        }
        return user;
    }
}
