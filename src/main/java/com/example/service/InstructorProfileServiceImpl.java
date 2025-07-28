package com.example.service;

import com.example.model.*;
import com.example.repository.JpaInstructorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InstructorProfileServiceImpl implements InstructorProfileService {
    private final JpaInstructorRepository instructorRepo;

    public InstructorProfileServiceImpl(JpaInstructorRepository instructorRepo) {
        this.instructorRepo = instructorRepo;
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorRepo.findAll();
    }

    @Override
    public Optional<Instructor> getInstructorById(Long instructorId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");

        return instructorRepo.findById(instructorId);
    }
}