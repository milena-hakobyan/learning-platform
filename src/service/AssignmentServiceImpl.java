package com.example.service;

import com.example.model.Assignment;
import com.example.model.Material;
import com.example.model.Submission;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    private final CourseRepository courseRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;

    public AssignmentServiceImpl(CourseRepository courseRepo, AssignmentRepository assignmentRepo, SubmissionRepository submissionRepo) {
        this.courseRepo = courseRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
    }

    @Override
    public void addAssignmentToCourse(Long courseId, Assignment assignment) {
        courseRepo.ensureCourseExists(courseId);

        if (assignment.getCourse().getId() != null && !assignment.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Assignment already belongs to a different course.");
        }
        assignmentRepo.save(assignment);
    }

    @Override
    public void removeAssignmentFromCourse(Long courseId, Long assignmentId) {
        courseRepo.ensureCourseExists(courseId);

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Given course doesn't include an assignment with id " + assignmentId);
        }

        submissionRepo.findAllByAssignmentId(assignmentId)
                .forEach(submission -> submissionRepo.delete(submission.getId()));

        assignmentRepo.delete(assignmentId);
    }


    @Override
    public void addMaterialToAssignment(Long assignmentId, Material material) {
        Objects.requireNonNull(material, "Material cannot be null");
        assignmentRepo.ensureAssignmentExists(assignmentId);

        assignmentRepo.addMaterial(assignmentId, material);
    }

    @Override
    public void removeMaterialFromAssignment(Long assignmentId, Long materialId) {
        Objects.requireNonNull(materialId, "Material ID cannot be null");
        assignmentRepo.ensureAssignmentExists(assignmentId);

        assignmentRepo.removeMaterial(assignmentId, materialId);
    }

    @Override
    public List<Assignment> getAssignmentsForCourse(Long courseId) {
        courseRepo.ensureCourseExists(courseId);

        return assignmentRepo.findAllByCourseId(courseId);
    }
}