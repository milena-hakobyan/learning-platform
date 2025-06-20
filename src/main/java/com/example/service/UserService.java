package com.example.service;

import com.example.model.*;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Integer id);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByUserName(String userName);

    List<User> getUsersByRole(Role role);

    Student registerStudent(String username, String firstName, String lastName, String email, String rawPassword);

    Instructor registerInstructor(String username, String firstName, String lastName, String email, String rawPassword, String bio);

    Optional<User> login(String email, String password);

    void updateUser(User user);

    void deleteUser(Integer userId);

    void deactivateUser(Integer userId);
}