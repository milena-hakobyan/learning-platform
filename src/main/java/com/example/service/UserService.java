package com.example.service;

import com.example.dto.user.UserResponse;
import com.example.dto.user.UserUpdateRequest;
import com.example.model.*;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponse getById(Long id);

    UserResponse getByEmail(String email);

    UserResponse getByUsername(String username);

    List<UserResponse> getAllByRole(Role role);

    UserResponse login(String email, String password);

    UserResponse update(Long userId, UserUpdateRequest user);

    void delete(Long userId);

    void deactivate(Long userId);
}