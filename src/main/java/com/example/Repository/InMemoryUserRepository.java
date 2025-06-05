package com.example.Repository;

import com.example.Model.Role;
import com.example.Model.Student;
import com.example.Model.Submission;
import com.example.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void save(User entity) {
        users.put(entity.getUserId(), entity);
    }

    @Override
    public void delete(String id) {
        users.remove(id);
    }

    @Override
    public User findById(String id) {
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUserName().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findByRole(Role role) {
        return users.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .collect(Collectors.toList());
    }

}
