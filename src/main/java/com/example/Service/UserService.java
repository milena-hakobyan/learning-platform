package com.example.Service;

import com.example.Model.Role;
import com.example.Model.User;

import java.util.List;

public interface UserService {
    User getUserById(String id);

    User getUserByEmail(String email);

    User getUserByUserName(String userName);

    List<User> getUsersByRole(Role role);

    User registerUser(String name, String username, String email, String rawPassword, Role role);

    User login(String email, String password);

    void updateUser(User user);

    void deleteUser(String id);
}