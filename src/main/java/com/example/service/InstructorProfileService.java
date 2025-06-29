package com.example.service;

import com.example.model.*;

import java.util.List;
import java.util.Optional;

public interface InstructorProfileService {
    Optional<Instructor> getInstructorById(Integer instructorId);

    List<Instructor> getAllInstructors();
}