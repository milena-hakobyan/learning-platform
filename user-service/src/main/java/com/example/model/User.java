package com.example.model;

import com.example.utils.StringUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
public class User {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", unique = true, nullable = false)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private Role role;

    @Column(name = "password_hash")
    private String password;

    private LocalDateTime lastLogin;

    @Column(name = "is_active")
    private boolean active = true;

    public User() {}

    public User(String username, String firstName, String lastName, String email, String password, Role role, LocalDateTime lastLogin) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = StringUtils.applySha256(password);
        this.role = role;
        this.lastLogin = lastLogin;
        this.active = true;
    }

    public void setUsername(String username) { this.username = username; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setEmail(String email) { this.email = email; }

    public void setRole(Role role) { this.role = role; }

    public void setPassword(String password) { this.password = StringUtils.applySha256(password); }

    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public void setActive(boolean active) { this.active = active; }
}
