package com.example.Repository;

import com.example.Model.Role;
import com.example.Model.User;
import java.util.List;

public interface UserRepository extends CrudRepository<User, String> {
    User findByEmail(String email);

    User findByUsername(String username);

    List<User> findByRole(Role role);
}
