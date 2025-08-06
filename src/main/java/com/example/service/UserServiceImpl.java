package com.example.service;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaStudentRepository;
import com.example.repository.JpaUserRepository;
import com.example.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final JpaUserRepository userRepo;
    private final JpaStudentRepository studentRepo;
    private final JpaInstructorRepository instructorRepo;

    public UserServiceImpl(JpaUserRepository userRepo, JpaStudentRepository studentRepo, JpaInstructorRepository instructorRepo) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
    }

    @Override
    public void updateUser(User user) {
        Objects.requireNonNull(user, "UserService: user cannot be null");
        if (!userRepo.existsById(user.getId())) {
            throw new IllegalArgumentException("Cannot update non-existent user with ID: " + user.getId());
        }
        userRepo.save(user);
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
    public Optional<User> getUserById(Long userId) {
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
    public void deleteUser(Long userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        if (!userRepo.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        studentRepo.findById(userId).ifPresent(student -> studentRepo.deleteById(userId));
        instructorRepo.findById(userId).ifPresent(instructor -> instructorRepo.deleteById(userId));

        userRepo.deleteById(userId);
    }

    @Override
    public void deactivateUser(Long userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setActive(false);
        userRepo.save(user);
    }
}