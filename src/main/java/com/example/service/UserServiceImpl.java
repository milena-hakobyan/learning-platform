package com.example.service;

import com.example.model.Instructor;
import com.example.model.Role;
import com.example.model.Student;
import com.example.model.User;
import com.example.repository.InstructorRepository;
import com.example.repository.StudentRepository;
import com.example.repository.UserRepository;
import com.example.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
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
    public void updateUser(User user) {
        Objects.requireNonNull(user, "UserService: user cannot be null");
        userRepo.ensureUserExists(user.getId());

        userRepo.update(user);
    }

    @Override
    public Optional<User> login(String email, String password) {
        Objects.requireNonNull(email, "UserService: email cannot be null");
        Objects.requireNonNull(password, "UserService: password cannot be null");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("UserService: user with given email does not exist"));

        if (!user.getPassword().equals(StringUtils.applySha256(password))) {
            throw new IllegalArgumentException("UserService: incorrect password");
        }

        return Optional.of(user);
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        Objects.requireNonNull(userId, "UserService: user ID cannot be null");

        return userRepo.findById(userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        Objects.requireNonNull(email, "UserService: email cannot be null");

        return userRepo.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByUserName(String userName) {
        Objects.requireNonNull(userName, "UserService: username cannot be null");

        return userRepo.findByUsername(userName);
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        Objects.requireNonNull(role, "UserService: role cannot be null");

        return userRepo.findAllByRole(role);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepo.ensureUserExists(userId);

        studentRepo.findById(userId).ifPresent(student -> studentRepo.delete(userId));
        instructorRepo.findById(userId).ifPresent(instructor -> instructorRepo.delete(userId));

        userRepo.delete(userId);
    }

    @Override
    public void deactivateUser(Integer userId) {
        userRepo.ensureUserExists(userId);

        userRepo.deactivateUser(userId);
    }
}