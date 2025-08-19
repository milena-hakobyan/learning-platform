package com.example.repository;

import com.example.model.Role;
import com.example.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Page<User> findAllByRole(Role role, Pageable pageable);

    @Query("""
        SELECT COUNT(u) = 0 FROM User u
        WHERE u.username = :username OR u.email = :email
    """)
    boolean isUsernameAndEmailAvailable(String username, String email);
}
