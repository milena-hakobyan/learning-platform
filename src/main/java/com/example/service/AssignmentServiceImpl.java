package com.example.service;

import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.assignment.CreateAssignmentRequest;
import com.example.dto.assignment.UpdateAssignmentRequest;
import com.example.dto.material.CreateMaterialRequest;
import com.example.dto.material.MaterialResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.AssignmentMapper;
import com.example.mapper.MaterialMapper;
import com.example.model.Assignment;
import com.example.model.Course;
import com.example.model.Instructor;
import com.example.model.Material;
import com.example.repository.JpaAssignmentRepository;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaSubmissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.plaf.PanelUI;
import java.util.List;
import java.util.Objects;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    private final JpaCourseRepository courseRepo;
    private final JpaAssignmentRepository assignmentRepo;
    private final JpaSubmissionRepository submissionRepo;
    private final JpaInstructorRepository instructorRepo;
    private final AssignmentMapper assignmentMapper;
    private final MaterialMapper materialMapper;

    public AssignmentServiceImpl(JpaCourseRepository courseRepo, JpaAssignmentRepository assignmentRepo, JpaSubmissionRepository submissionRepo, JpaInstructorRepository instructorRepo, AssignmentMapper assignmentMapper, MaterialMapper materialMapper) {
        this.courseRepo = courseRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.instructorRepo = instructorRepo;
        this.assignmentMapper = assignmentMapper;
        this.materialMapper = materialMapper;
    }

    @Override
    public AssignmentResponse createAssignment(Long courseId, CreateAssignmentRequest request) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course with ID " + courseId + " doesn't exist."));

        Assignment assignment = assignmentMapper.toEntity(request, course);

        return assignmentMapper.toDto(assignmentRepo.save(assignment));
    }

    @Override
    public AssignmentResponse getAssignmentById(Long assignmentId) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment with ID " + assignmentId + " not found"));

        return assignmentMapper.toDto(assignment);
    }


    @Override
    public AssignmentResponse updateAssignment(Long assignmentId, UpdateAssignmentRequest dto) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment with ID " + assignmentId + " not found"));

        assignmentMapper.updateEntity(dto, assignment);
        Assignment saved = assignmentRepo.save(assignment);

        return assignmentMapper.toDto(saved);
    }


    @Override
    public void deleteAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        submissionRepo.deleteAllByAssignmentId(assignmentId);

        assignmentRepo.deleteById(assignmentId);
    }


    @Override
    public MaterialResponse addMaterialToAssignment(Long assignmentId, CreateMaterialRequest dto) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment doesn't exist"));

        Instructor instructor = instructorRepo.findById(dto.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor doesn't exist"));

        Material material = materialMapper.toEntity(dto, instructor);
        assignment.addMaterial(material);
        assignmentRepo.save(assignment);

        return materialMapper.toDto(material);
    }

    @Override
    public void removeMaterialFromAssignment(Long assignmentId, Long materialId) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment doesn't exist"));

        boolean removed = assignment.getMaterials().removeIf(m -> m.getId().equals(materialId));
        if (!removed) {
            throw new ResourceNotFoundException("Material not found in assignment");
        }

        assignmentRepo.save(assignment);
    }


    @Override
    public Page<AssignmentResponse> getAssignmentsForCourse(Long courseId, Pageable pageable) {
        if (!courseRepo.existsById(courseId)) {
            throw new ResourceNotFoundException("Course with ID " + courseId + " doesn't exist.");
        }

        return assignmentRepo.findAllByCourseId(courseId, pageable)
                .map(assignmentMapper::toDto);
    }
}