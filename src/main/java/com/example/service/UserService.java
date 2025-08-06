package com.example.service;

import com.example.model.*;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByUserName(String userName);

    List<User> getUsersByRole(Role role);

    Optional<User> login(String email, String password);

    void updateUser(User user);

    void deleteUser(Long userId);

    void deactivateUser(Long userId);
}