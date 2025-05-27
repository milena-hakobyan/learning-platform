package com.example.Service;

import com.example.Model.Instructor;
import com.example.Model.Role;
import com.example.Model.Student;
import com.example.Model.User;
import com.example.Repository.UserRepository;
import com.example.Utils.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

   
    @Override
    public User registerUser(String name, String username, String email, String rawPassword, Role role) {
        if (userRepo.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        String hashedPassword = StringUtils.applySha256(rawPassword);
        User user = null;
        switch (role) {
            case STUDENT:
                user = new Student(name, username, email, hashedPassword);
                break;
            case INSTRUCTOR:
                user = new Instructor(name, username, email, hashedPassword);
                break;
        }

        userRepo.save(user);
        return user;
    }



    public User login(String email, String password) {
        User user = userRepo.findByEmail(email);
        if (user == null || !user.getPassword().equals(StringUtils.applySha256(password))) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }

    @Override
    public void updateUser(User user) {
        if (userRepo.findById(user.getUserId()) == null) {
            throw new IllegalArgumentException("User not found");
        }
        userRepo.save(user);
    }

    @Override
    public User getUserById(String userId) {
        User user = userRepo.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    @Override
    public User getUserByUserName(String userName) {
        User user = userRepo.findByUsername(userName);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepo.findByRole(role);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepo.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        userRepo.delete(id);
    }


}
