package com.example.model;

import java.time.LocalDateTime;

import com.example.utils.StringUtils;

public class User {
    private final Integer userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String password;
    private LocalDateTime lastLogin;
    private boolean isActive;

    public User(Integer userId, String userName, String firstName, String lastName, String email, String password, Role role, LocalDateTime lastLogin, boolean isActive) {
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }

    // this constructor is for user-registration
    public User(String userName, String firstName, String lastName, String email, String password,
                Role role, LocalDateTime lastLogin) {
        this(null, userName, firstName, lastName, email, password, role, lastLogin, true);
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", lastLogin=" + lastLogin +
                ", isActive=" + isActive +
                '}';
    }
}
