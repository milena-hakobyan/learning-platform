package com.example.repository.jpa;

import com.example.model.Role;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findAllByRole(Role role);

    @Query("""
        SELECT COUNT(u) = 0 FROM User u
        WHERE u.username = :username OR u.email = :email
    """)
    boolean isUsernameAndEmailAvailable(@Param("username") String username, @Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.active = false WHERE u.id = :userId")
    void deactivateUser(@Param("userId") Integer userId);
}
