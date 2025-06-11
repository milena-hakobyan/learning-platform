package com.example.Service;

import com.example.Model.*;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Integer id);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByUserName(String userName);

    List<User> getUsersByRole(Role role);

    Student registerStudent(Integer userId, String username, String firstName, String lastName, String email,
                            String rawPassword, Double progressPercentage, Integer completedCourses, Integer currentCourses);

    Instructor registerInstructor(Integer userId, String username, String firstName, String lastName, String email,
                                  String rawPassword, String bio, int totalCoursesCreated, double rating, boolean isVerified);

    Optional<User> login(String email, String password);

    void updateUser(User user);

    void deleteUser(Integer userId);

    void deactivateUser(Integer userId);
}