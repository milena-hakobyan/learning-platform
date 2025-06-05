package com.example.Service;

import com.example.Model.Instructor;
import com.example.Model.Role;
import com.example.Model.Student;
import com.example.Model.User;
import com.example.Repository.UserRepository;
import com.example.Utils.StringUtils;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User registerUser(String name, String username, String email, String rawPassword, Role role) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hashedPassword = StringUtils.applySha256(rawPassword);
        User user = switch (role) {
            case STUDENT -> new Student(name, username, email, hashedPassword);
            case INSTRUCTOR -> new Instructor(name, username, email, hashedPassword);
        };

        userRepo.save(user);
        return user;
    }

    @Override
    public User login(String email, String password) {
        return userRepo.findByEmail(email)
                .filter(u -> u.getPassword().equals(StringUtils.applySha256(password)))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    }

    @Override
    public void updateUser(User user) {
        getExistingUser(user.getUserId()); // will throw if not found
        userRepo.save(user);
    }

    @Override
    public User getUserById(String userId) {
        return getExistingUser(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public User getUserByUserName(String userName) {
        return userRepo.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepo.findByRole(role);
    }

    @Override
    public void deleteUser(String id) {
        getExistingUser(id); // will throw if not found
        userRepo.delete(id);
    }

    private User getExistingUser(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
