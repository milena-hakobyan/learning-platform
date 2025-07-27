package com.example.service;

import com.example.model.Assignment;
import com.example.model.Material;
import com.example.repository.JpaAssignmentRepository;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaSubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    private final JpaCourseRepository courseRepo;
    private final JpaAssignmentRepository assignmentRepo;
    private final JpaSubmissionRepository submissionRepo;

    public AssignmentServiceImpl(JpaCourseRepository courseRepo, JpaAssignmentRepository assignmentRepo, JpaSubmissionRepository submissionRepo) {
        this.courseRepo = courseRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
    }

    @Override
    public void addAssignmentToCourse(Long courseId, Assignment assignment) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(assignment, "Assignment cannot be null");

        if (!courseRepo.existsById(courseId)) {
            throw new IllegalArgumentException("Course with ID " + courseId + " doesn't exist.");
        }

        if (assignment.getCourse().getId() != null && !assignment.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Assignment already belongs to a different course.");
        }
        assignmentRepo.save(assignment);
    }

    @Override
    public void removeAssignmentFromCourse(Long courseId, Long assignmentId) {
        courseRepo.existsById(courseId);

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Given course doesn't include an assignment with id " + assignmentId);
        }

        submissionRepo.findAllByAssignmentId(assignmentId)
                .forEach(submission -> submissionRepo.deleteById(submission.getId()));

        assignmentRepo.deleteById(assignmentId);
    }


    @Override
    public void addMaterialToAssignment(Long assignmentId, Material material) {
        Objects.requireNonNull(material, "Material cannot be null");

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment doesn't exist"));

        assignment.addMaterial(material);
        assignmentRepo.save(assignment);
    }

    @Override
    public void removeMaterialFromAssignment(Long assignmentId, Long materialId) {
        Objects.requireNonNull(materialId, "Material ID cannot be null");

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment doesn't exist"));

        Material material = assignment.getMaterials().stream()
                .filter(m -> m.getId().equals(materialId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Material not found in lesson"));

        assignment.removeMaterial(material);
        assignmentRepo.save(assignment);
    }

    @Override
    public List<Assignment> getAssignmentsForCourse(Long courseId) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");

        if (!courseRepo.existsById(courseId)) {
            throw new IllegalArgumentException("Course with ID " + courseId + " doesn't exist.");
        }

        return assignmentRepo.findAllByCourseId(courseId);
    }
}