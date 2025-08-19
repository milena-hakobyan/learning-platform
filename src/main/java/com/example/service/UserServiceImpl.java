package com.example.service;

import com.example.dto.user.UserResponse;
import com.example.dto.user.UserUpdateRequest;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.UserMapper;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaStudentRepository;
import com.example.repository.JpaUserRepository;
import com.example.utils.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final JpaUserRepository userRepo;
    private final JpaStudentRepository studentRepo;
    private final JpaInstructorRepository instructorRepo;
    private final UserMapper userMapper;

    public UserServiceImpl(JpaUserRepository userRepo, JpaStudentRepository studentRepo, JpaInstructorRepository instructorRepo, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse update(Long userId, UserUpdateRequest request) {
        User user = userRepo.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userMapper.updateEntity(request, user);
        userRepo.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public UserResponse login(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with given email does not exist"));

        if (!user.getPassword().equals(StringUtils.applySha256(password))) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return userMapper.toDto(user);
    }

    @Override
    public UserResponse getById(Long userId) {

        return userRepo.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public UserResponse getByEmail(String email) {

        return userRepo.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserResponse getByUsername(String username) {

        return userRepo.findByUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public Page<UserResponse> getAllByRole(Role role, Pageable pageable) {

        return userRepo.findAllByRole(role, pageable)
                .map(userMapper::toDto);
    }

    @Override
    public void delete(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        userRepo.deleteById(userId);
    }

    @Override
    public void deactivate(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setActive(false);
        userRepo.save(user);
    }
}