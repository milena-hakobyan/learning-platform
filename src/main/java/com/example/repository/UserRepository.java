package com.example.repository;

import com.example.model.Role;
import com.example.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    void deactivateUser(Integer userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findAllByRole(Role role);

    void ensureUserExists(Integer userId);

    void ensureEmailAndUsernameAvailable(String username, String email);
}