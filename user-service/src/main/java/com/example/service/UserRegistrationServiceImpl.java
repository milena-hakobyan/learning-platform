package com.example.service;

import com.example.dto.user.RegisterInstructorRequest;
import com.example.dto.user.RegisterStudentRequest;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.JpaUserRepository;
import com.example.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final JpaUserRepository userRepo;

    public UserRegistrationServiceImpl(JpaUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Long registerStudent(RegisterStudentRequest request) {
        userRepo.isUsernameAndEmailAvailable(request.getUsername(), request.getEmail());

        User user = new User(
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                Role.STUDENT,
                LocalDateTime.now()
        );

        return userRepo.save(user).getId();
    }

    @Override
    public Long registerInstructor(RegisterInstructorRequest request) {
        userRepo.isUsernameAndEmailAvailable(request.getUsername(), request.getEmail());

        User user = new User(
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                Role.INSTRUCTOR,
                LocalDateTime.now()
        );

        return userRepo.save(user).getId();
    }
}
