package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import com.example.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
class AssignmentServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private InstructorRepository instructorRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private AssignmentRepository assignmentRepo;

    @Autowired
    private SubmissionRepository submissionRepo;

    @Autowired
    private DatabaseConnection dbConnection;

    private Integer courseId;
    private Integer studentId;
    private Assignment savedAssignment;

    @BeforeEach
    void setup() {
        Instructor instructor = new Instructor(
                null,
                "janedoe",
                "Jane",
                "Doe",
                "jane@example.com",
                "pass123",
                LocalDateTime.now(),
                true,
                "Expert instructor",
                10,
                4.9,
                true
        );
        instructor = instructorRepo.save(instructor);
        Student student = new Student(
                null,
                "johndoe", "John", "Doe",
                "john@example.com", "pass123",
                LocalDateTime.now(), true,
                0.0, 0, 0
        );
        student = studentRepo.save(student); // Make sure to autowire this repo
        studentId = student.getId();

        Course course = new Course(
                null,
                "Test Course",
                "Description",
                "Category",
                "http://example.com",
                instructor.getId() // instructorId, dummy
        );
        course = courseRepo.save(course);
        courseId = course.getId();

        Assignment assignment = new Assignment(
                null,
                "Assignment 1",
                "Desc 1",
                LocalDateTime.now().plusDays(7),
                100.0,
                courseId
        );
        savedAssignment = assignmentRepo.save(assignment);
    }

    @Test
    void addAssignmentToCourse_shouldSaveAssignment() {
        Assignment assignment = new Assignment(
                null,
                "New Assignment",
                "Desc",
                LocalDateTime.now().plusDays(5),
                50.0,
                courseId
        );

        assignmentService.addAssignmentToCourse(courseId, assignment);

        List<Assignment> assignments = assignmentRepo.findAllByCourseId(courseId);
        assertTrue(assignments.stream().anyMatch(a -> a.getTitle().equals("New Assignment")));
    }

    @Test
    void addAssignmentToCourse_shouldThrow_whenAssignmentHasDifferentCourseId() {
        Assignment assignment = new Assignment(
                null,
                "Wrong Course",
                "Desc",
                LocalDateTime.now().plusDays(5),
                50.0,
                courseId + 1000
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            assignmentService.addAssignmentToCourse(courseId, assignment);
        });

        assertTrue(ex.getMessage().contains("already belongs to a different course"));
    }

    @Test
    void addAssignmentToCourse_shouldThrow_whenCourseDoesNotExist() {
        Assignment assignment = new Assignment(
                null,
                "Test",
                "Desc",
                LocalDateTime.now().plusDays(5),
                50.0,
                null
        );

        InvalidDataAccessApiUsageException ex = assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            assignmentService.addAssignmentToCourse(9999, assignment);
        });

        assertTrue(ex.getMessage().contains("Course"));
    }

    @Test
    void removeAssignmentFromCourse_shouldDeleteAssignmentAndSubmissions() {
        Submission submission = new Submission(
                null,
                studentId,
                savedAssignment.getId(),
                "contentLink here",
                LocalDateTime.now()
        );
        submission = submissionRepo.save(submission);

        assignmentService.removeAssignmentFromCourse(courseId, savedAssignment.getId());

        assertTrue(assignmentRepo.findById(savedAssignment.getId()).isEmpty());
        assertTrue(submissionRepo.findById(submission.getSubmissionId()).isEmpty());
    }

    @Test
    void removeAssignmentFromCourse_shouldThrow_whenCourseDoesNotExist() {
        InvalidDataAccessApiUsageException ex = assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            assignmentService.removeAssignmentFromCourse(9999, savedAssignment.getId());
        });

        assertTrue(ex.getMessage().contains("Course"));
    }

    @Test
    void removeAssignmentFromCourse_shouldThrow_whenAssignmentNotFound() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            assignmentService.removeAssignmentFromCourse(courseId, 9999);
        });

        assertTrue(ex.getMessage().contains("Assignment not found"));
    }

    @Test
    void removeAssignmentFromCourse_shouldThrow_whenAssignmentNotBelongToCourse() {
        Course anotherCourse = courseRepo.save(new Course(null, "Another Course", "Desc", "Cat", "url", 1));
        Assignment anotherAssignment = assignmentRepo.save(new Assignment(
                null,
                "Another Assignment",
                "Desc",
                LocalDateTime.now().plusDays(5),
                50.0,
                anotherCourse.getId()
        ));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            assignmentService.removeAssignmentFromCourse(courseId, anotherAssignment.getId());
        });

        assertTrue(ex.getMessage().contains("doesn't include an assignment"));
    }

    @Test
    void addMaterialToAssignment_shouldAddMaterial() {
        Material material = new Material(
                null,
                "Material Title",
                "Video",
                "Category A",
                "http://material.url",
                1,
                LocalDateTime.now()
        );

        assignmentService.addMaterialToAssignment(savedAssignment.getId(), material);

        List<Material> materials = assignmentRepo.findMaterialsByAssignmentId(savedAssignment.getId());
        assertEquals(1, materials.size());
        assertEquals("Material Title", materials.get(0).getTitle());
    }


    @Test
    void addMaterialToAssignment_shouldThrow_whenMaterialIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            assignmentService.addMaterialToAssignment(savedAssignment.getId(), null);
        });

        assertTrue(ex.getMessage().contains("Material cannot be null"));
    }

    @Test
    void addMaterialToAssignment_shouldThrow_whenAssignmentDoesNotExist() {
        Material material = new Material(
                null,
                "Title",
                "Document",
                "Category",
                "http://url",
                1,
                LocalDateTime.now()
        );

        InvalidDataAccessApiUsageException ex = assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            assignmentService.addMaterialToAssignment(9999, material);
        });

        assertTrue(ex.getMessage().contains("Assignment"));
    }

    @Test
    void removeMaterialFromAssignment_shouldRemoveMaterial() {
        Material material = new Material(
                null,
                "Material Title",
                "Video",
                "Category A",
                "http://material.url",
                1,
                LocalDateTime.now()
        );
        assignmentRepo.addMaterial(savedAssignment.getId(), material);

        List<Material> materialsBefore = assignmentRepo.findMaterialsByAssignmentId(savedAssignment.getId());
        assertEquals(1, materialsBefore.size());

        Integer materialId = materialsBefore.get(0).getId(); // real ID

        assignmentService.removeMaterialFromAssignment(savedAssignment.getId(), materialId);

        List<Material> materialsAfter = assignmentRepo.findMaterialsByAssignmentId(savedAssignment.getId());

        assertTrue(materialsAfter.isEmpty(), "Material should be removed from assignment");
    }


    @Test
    void removeMaterialFromAssignment_shouldThrow_whenMaterialIdIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            assignmentService.removeMaterialFromAssignment(savedAssignment.getId(), null);
        });

        assertTrue(ex.getMessage().contains("Material ID cannot be null"));
    }

    @Test
    void removeMaterialFromAssignment_shouldThrow_whenAssignmentDoesNotExist() {
        InvalidDataAccessApiUsageException ex = assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            assignmentService.removeMaterialFromAssignment(9999, 1);
        });

        assertTrue(ex.getMessage().contains("Assignment"));
    }

    @Test
    void getAssignmentsForCourse_shouldReturnAssignments() {
        List<Assignment> assignments = assignmentService.getAssignmentsForCourse(courseId);

        assertFalse(assignments.isEmpty());
        assertTrue(assignments.stream().anyMatch(a -> a.getId().equals(savedAssignment.getId())));
    }

    @Test
    void getAssignmentsForCourse_shouldThrow_whenCourseDoesNotExist() {
        InvalidDataAccessApiUsageException ex = assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            assignmentService.getAssignmentsForCourse(9999);
        });

        assertTrue(ex.getMessage().contains("Course"));
    }
    @AfterEach
    void  truncateBeforeEach(@Autowired DatabaseConnection dbConnection) {
        dbConnection.execute("TRUNCATE TABLE submissions, assignments, courses, instructors, students, materials, users RESTART IDENTITY CASCADE");
    }

}
