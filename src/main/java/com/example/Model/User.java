package com.example.Model;

import java.time.LocalDateTime;
import java.util.UUID;
import com.example.Utils.StringUtils;

public class User {
    private final String userId;
    private String name;
    private String userName;
    private Role role;
    private String email;
    private String password;
    private LocalDateTime registrationDate;

    public User(String name, String userName, String email, String password, Role role) {
        this.name = name;
        this.userId = UUID.randomUUID().toString();
        this.userName = userName;
        this.email = email;
        this.password = password; // Store hashed password
        this.role = role;
        this.registrationDate = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = StringUtils.applySha256(password);
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
