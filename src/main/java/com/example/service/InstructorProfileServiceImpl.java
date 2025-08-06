package com.example.service;

import com.example.dto.instructor.InstructorResponse;
import com.example.mapper.InstructorMapper;
import com.example.model.*;
import com.example.repository.JpaInstructorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InstructorProfileServiceImpl implements InstructorProfileService {
    private final JpaInstructorRepository instructorRepo;
    private final InstructorMapper instructorMapper;

    public InstructorProfileServiceImpl(JpaInstructorRepository instructorRepo, InstructorMapper instructorMapper) {
        this.instructorRepo = instructorRepo;
        this.instructorMapper = instructorMapper;
    }

    @Override
    public List<InstructorResponse> getAllInstructors() {

        return instructorRepo.findAll()
                .stream()
                .map(instructorMapper::toDto)
                .toList();
    }

    @Override
    public Optional<InstructorResponse> getInstructorById(Long instructorId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");

        return instructorRepo.findById(instructorId)
                .map(instructorMapper::toDto);
    }
}