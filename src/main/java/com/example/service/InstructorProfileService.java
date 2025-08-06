package com.example.service;

import com.example.dto.instructor.InstructorResponse;
import com.example.model.*;

import java.util.List;
import java.util.Optional;

public interface InstructorProfileService {
    Optional<InstructorResponse> getInstructorById(Long instructorId);

    List<InstructorResponse> getAllInstructors();
}