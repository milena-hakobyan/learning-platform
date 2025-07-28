package com.example.service;

import com.example.model.Instructor;
import com.example.model.Role;
import com.example.model.Student;
import com.example.model.User;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaStudentRepository;
import com.example.repository.JpaUserRepository;
import com.example.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final JpaUserRepository userRepo;
    private final JpaStudentRepository studentRepo;
    private final JpaInstructorRepository instructorRepo;

    public RegistrationServiceImpl(JpaUserRepository userRepo, JpaStudentRepository studentRepo, JpaInstructorRepository instructorRepo) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
    }

    @Override
    public Student registerStudent(String username, String firstName, String lastName, String email, String rawPassword) {
        Objects.requireNonNull(username, "RegistrationService: username cannot be null");
        Objects.requireNonNull(firstName, "RegistrationService: firstName cannot be null");
        Objects.requireNonNull(lastName, "RegistrationService: lastName cannot be null");
        Objects.requireNonNull(email, "RegistrationService: email cannot be null");
        Objects.requireNonNull(rawPassword, "RegistrationService: password cannot be null");

        userRepo.isUsernameAndEmailAvailable(username, email);

        User user = new User(username, firstName, lastName, email, StringUtils.applySha256(rawPassword), Role.STUDENT, LocalDateTime.now());

        user = userRepo.save(user);

        Student student = new Student(user);

        return studentRepo.save(student);
    }

    @Override
    public Instructor registerInstructor(String username, String firstName, String lastName, String email, String rawPassword, String bio) {
        Objects.requireNonNull(username, "RegistrationService: username cannot be null");
        Objects.requireNonNull(firstName, "RegistrationService: firstName cannot be null");
        Objects.requireNonNull(lastName, "RegistrationService: lastName cannot be null");
        Objects.requireNonNull(email, "RegistrationService: email cannot be null");
        Objects.requireNonNull(rawPassword, "RegistrationService: password cannot be null");
        Objects.requireNonNull(bio, "RegistrationService: bio cannot be null");

        userRepo.isUsernameAndEmailAvailable(username, email);

        User user = new User(username, firstName, lastName, email, StringUtils.applySha256(rawPassword), Role.INSTRUCTOR, LocalDateTime.now());

        user = userRepo.save(user);

        Instructor instructor = new Instructor(user, bio);

        return instructorRepo.save(instructor);
    }
}