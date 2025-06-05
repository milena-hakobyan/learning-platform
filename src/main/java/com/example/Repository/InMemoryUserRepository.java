package com.example.Repository;

import com.example.Model.Role;
import com.example.Model.Student;
import com.example.Model.Submission;
import com.example.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void save(User entity) {
        if (entity == null){
            throw new IllegalArgumentException("User object cannot be null");
        }
        if (entity.getUserId() == null){
            throw new IllegalArgumentException("User Id cannot be null");
        }
        users.put(entity.getUserId(), entity);
    }

    @Override
    public void delete(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        users.remove(id);
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return Optional.ofNullable(users.get(id));
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return users.values().stream()
                .filter(user -> {
                    String userEmail = user.getEmail();
                    return userEmail != null && userEmail.equalsIgnoreCase(email);
                })
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        return users.values().stream()
                .filter(user -> {
                    String userName = user.getUserName();
                    return userName != null && userName.equalsIgnoreCase(username);
                })
                .findFirst();
    }

    @Override
    public List<User> findByRole(Role role) {
        if (role == null){
            throw new IllegalArgumentException("Role cannot be null");
        }
        return users.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .collect(Collectors.toList());
    }

}
