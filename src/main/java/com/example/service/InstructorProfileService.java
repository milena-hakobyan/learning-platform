package com.example.service;

import com.example.dto.instructor.InstructorResponse;
import com.example.dto.instructor.UpdateInstructorRequest;
import com.example.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InstructorProfileService {
    InstructorResponse getInstructorById(Long instructorId);

    Page<InstructorResponse> getAllInstructors(Pageable pageable);

    InstructorResponse updateInstructor(Long instructorId, UpdateInstructorRequest request);
}