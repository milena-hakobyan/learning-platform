package com.example.Service;

import com.example.Model.Instructor;
import com.example.Model.Role;
import com.example.Model.Student;
import com.example.Model.User;
import com.example.Repository.InstructorRepository;
import com.example.Repository.StudentRepository;
import com.example.Repository.UserRepository;
import com.example.Utils.InputValidationUtils;
import com.example.Utils.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final InstructorRepository instructorRepo;

    public UserServiceImpl(UserRepository userRepo, StudentRepository studentRepo, InstructorRepository instructorRepo) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
    }

    @Override
    public Student registerStudent(Integer userId, String username, String firstName, String lastName, String email,
                                   String rawPassword, Double progressPercentage, Integer completedCourses, Integer currentCourses) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already registered");
        }

        String hashedPassword = StringUtils.applySha256(rawPassword);
        Student student = new Student(userId, username, firstName, lastName, email, hashedPassword,
                LocalDateTime.now(), true, progressPercentage, completedCourses, currentCourses);

        userRepo.save(student);
        studentRepo.save(student);

        return student;
    }

    @Override
    public Instructor registerInstructor(Integer userId, String username, String firstName, String lastName, String email,
                                         String rawPassword, String bio, int totalCoursesCreated, double rating, boolean isVerified) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already registered");
        }

        String hashedPassword = StringUtils.applySha256(rawPassword);
        Instructor instructor = new Instructor(userId, username, firstName, lastName, email, hashedPassword,
                LocalDateTime.now(), true, bio, totalCoursesCreated, rating, isVerified);

        userRepo.save(instructor);
        instructorRepo.save(instructor);
        return instructor;
    }

    @Override
    public void updateUser(User user) {
        InputValidationUtils.requireNonNull(user, "User cannot be null");

        getExistingUser(user.getUserId());
        userRepo.update(user);
    }


    @Override
    public Optional<User> login(String email, String password) {
        InputValidationUtils.requireNonNull(email, "Email cannot be null");
        InputValidationUtils.requireNonNull(password, "Password cannot be null");

        return userRepo.findByEmail(email)
                .filter(u -> u.getPassword().equals(StringUtils.applySha256(password)));
    }


    @Override
    public Optional<User> getUserById(Integer userId) {
        InputValidationUtils.requireNonNull(userId, "User ID cannot be null");
        return userRepo.findById(userId);
    }


    @Override
    public Optional<User> getUserByEmail(String email) {
        InputValidationUtils.requireNonNull(email, "User email cannot be null");

        return userRepo.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByUserName(String userName) {
        InputValidationUtils.requireNonNull(userName, "Username cannot be null");

        return userRepo.findByUsername(userName);
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        InputValidationUtils.requireNonNull(role, "Role cannot be null");

        return userRepo.findByRole(role);
    }

    @Override
    public void deleteUser(Integer userId) {
        if (studentRepo.findById(userId).isPresent()) {
            studentRepo.delete(userId);
        }
        if (instructorRepo.findById(userId).isPresent()) {
            instructorRepo.delete(userId);
        }
        userRepo.delete(userId);
    }

    @Override
    public void deactivateUser(Integer userId) {
        getExistingUser(userId);
        userRepo.deactivateUser(userId);
    }

    private User getExistingUser(Integer userId) {
        InputValidationUtils.requireNonNull(userId, "Id cannot be null");

        return userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
