package com.example.service;

import com.example.model.Assignment;
import com.example.model.Material;
import com.example.model.Submission;
import com.example.repository.AssignmentRepository;
import com.example.repository.CourseRepository;
import com.example.repository.SubmissionRepository;
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
    public void addAssignmentToCourse(Integer courseId, Assignment assignment) {
        courseRepo.ensureCourseExists(courseId);

        if (assignment.getCourseId() != null && !assignment.getCourseId().equals(courseId)) {
            throw new IllegalArgumentException("Assignment already belongs to a different course.");
        }
        assignmentRepo.save(assignment);
    }

    @Override
    public void removeAssignmentFromCourse(Integer courseId, Integer assignmentId) {
        courseRepo.ensureCourseExists(courseId);

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getCourseId().equals(courseId)) {
            throw new IllegalArgumentException("Given course doesn't include an assignment with id " + assignmentId);
        }

        submissionRepo.findAllByAssignmentId(assignmentId)
                .forEach(submission -> submissionRepo.delete(submission.getSubmissionId()));

        assignmentRepo.delete(assignmentId);
    }


    @Override
    public void addMaterialToAssignment(Integer assignmentId, Material material) {
        Objects.requireNonNull(material, "Material cannot be null");
        assignmentRepo.ensureAssignmentExists(assignmentId);

        assignmentRepo.addMaterial(assignmentId, material);
    }

    @Override
    public void removeMaterialFromAssignment(Integer assignmentId, Integer materialId) {
        Objects.requireNonNull(materialId, "Material ID cannot be null");
        assignmentRepo.ensureAssignmentExists(assignmentId);

        assignmentRepo.removeMaterial(assignmentId, materialId);
    }

    @Override
    public List<Assignment> getAssignmentsForCourse(Integer courseId) {
        courseRepo.ensureCourseExists(courseId);

        return assignmentRepo.findAllByCourseId(courseId);
    }
}