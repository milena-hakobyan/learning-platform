package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorProfileServiceImpl implements InstructorProfileService {
    private final InstructorRepository instructorRepo;

    public InstructorProfileServiceImpl(InstructorRepository instructorRepo) {
        this.instructorRepo = instructorRepo;
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorRepo.findAll();
    }

    @Override
    public Optional<Instructor> getInstructorById(Integer instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return instructorRepo.findById(instructorId);
    }
}