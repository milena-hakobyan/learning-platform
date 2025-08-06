package com.example.service;

import com.example.model.*;

import java.util.List;
import java.util.Optional;

public interface InstructorProfileService {
    Optional<Instructor> getInstructorById(Long instructorId);

    List<Instructor> getAllInstructors();
}