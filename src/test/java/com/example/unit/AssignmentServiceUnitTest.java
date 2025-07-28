package com.example.unit;

import com.example.model.Assignment;
import com.example.model.Material;
import com.example.model.Submission;
import com.example.service.AssignmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceUnitTest {
    @Mock
    private CourseRepository courseRepo;
    @Mock
    private AssignmentRepository assignmentRepo;
    @Mock
    private SubmissionRepository submissionRepo;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    @Test
    void addAssignmentToCourse_shouldThrow_whenCourseNotFound() {
        Integer courseId = 101;
        Assignment assignment = new Assignment( "Description", null, LocalDateTime.now(), 100, courseId);

        doThrow(new IllegalArgumentException("Course does not exist")).when(courseRepo).ensureCourseExists(courseId);

        assertThrows(IllegalArgumentException.class, () ->
                assignmentService.addAssignmentToCourse(courseId, assignment));

        verify(courseRepo).ensureCourseExists(courseId);
        verifyNoInteractions(assignmentRepo);
    }

    @Test
    void addAssignmentToCourse_shouldThrow_whenAssignmentAlreadyBelongsToDifferentCourse() {
        Integer courseId = 101;
        Assignment assignment = new Assignment( "Description", null, LocalDateTime.now(), 100, 102);

        doNothing().when(courseRepo).ensureCourseExists(courseId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                assignmentService.addAssignmentToCourse(courseId, assignment)
        );

        assertEquals( "Assignment already belongs to a different course.", exception.getMessage());
        verify(courseRepo).ensureCourseExists(courseId);
        verifyNoInteractions(assignmentRepo);
    }

    @Test
    void addAssignmentToCourse_shouldSave_whenCourseExistsAndCourseIdMatches() {
        Integer courseId = 101;
        Assignment assignment = new Assignment( "Description", null, LocalDateTime.now(), 100, 101);

        doNothing().when(courseRepo).ensureCourseExists(courseId);

        assignmentService.addAssignmentToCourse(courseId, assignment);

        verify(courseRepo).ensureCourseExists(courseId);
        verify(assignmentRepo).save(assignment);

    }


    @Test
    void removeAssignmentFromCourse_shouldDeleteAssignmentAndSubmissions_whenValid() {
        Integer courseId = 101;
        Integer assignmentId = 202;
        Assignment assignment = new Assignment("Desc", null, LocalDateTime.now(), assignmentId, courseId);

        doNothing().when(courseRepo).ensureCourseExists(courseId);
        when(assignmentRepo.findById(assignmentId)).thenReturn(Optional.of(assignment));

        List<Submission> submissions = List.of(
                new Submission(1, 10, assignmentId, "link1", LocalDateTime.now()),
                new Submission(2, 11, assignmentId, "link2", LocalDateTime.now())
        );
        when(submissionRepo.findAllByAssignmentId(assignmentId)).thenReturn(submissions);
        doNothing().when(submissionRepo).delete(anyInt());
        doNothing().when(assignmentRepo).delete(assignmentId);

        assignmentService.removeAssignmentFromCourse(courseId, assignmentId);

        verify(courseRepo).ensureCourseExists(courseId);
        verify(assignmentRepo).findById(assignmentId);
        verify(submissionRepo).findAllByAssignmentId(assignmentId);
        for (Submission sub : submissions) {
            verify(submissionRepo).delete(sub.getSubmissionId());
        }
        verify(assignmentRepo).delete(assignmentId);
    }
    @Test
    void removeAssignmentFromCourse_shouldThrow_whenCourseDoesNotExist() {
        Integer courseId = 101;
        Integer assignmentId = 202;

        doThrow(new IllegalArgumentException("Course does not exist"))
                .when(courseRepo).ensureCourseExists(courseId);

        assertThrows(IllegalArgumentException.class, () ->
                assignmentService.removeAssignmentFromCourse(courseId, assignmentId)
        );

        verify(courseRepo).ensureCourseExists(courseId);
        verifyNoInteractions(assignmentRepo, submissionRepo);
    }

    @Test
    void removeAssignmentFromCourse_shouldThrow_whenAssignmentNotFound() {
        Integer courseId = 101;
        Integer assignmentId = 202;

        doNothing().when(courseRepo).ensureCourseExists(courseId);
        when(assignmentRepo.findById(assignmentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                assignmentService.removeAssignmentFromCourse(courseId, assignmentId)
        );

        verify(courseRepo).ensureCourseExists(courseId);
        verify(assignmentRepo).findById(assignmentId);
        verifyNoMoreInteractions(submissionRepo);
    }

    @Test
    void removeAssignmentFromCourse_shouldThrow_whenAssignmentCourseMismatch() {
        Integer courseId = 101;
        Integer assignmentId = 202;
        Assignment assignment = new Assignment("Desc", null, LocalDateTime.now(), assignmentId, 999); // wrong courseId

        doNothing().when(courseRepo).ensureCourseExists(courseId);
        when(assignmentRepo.findById(assignmentId)).thenReturn(Optional.of(assignment));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                assignmentService.removeAssignmentFromCourse(courseId, assignmentId)
        );

        assertEquals("Given course doesn't include an assignment with id " + assignmentId, ex.getMessage());

        verify(courseRepo).ensureCourseExists(courseId);
        verify(assignmentRepo).findById(assignmentId);
        verifyNoMoreInteractions(submissionRepo);
    }

    @Test
    void addMaterialToAssignment_shouldThrow_whenMaterialIsNull() {
        Integer assignmentId = 101;

        assertThrows(NullPointerException.class, () ->
                assignmentService.addMaterialToAssignment(assignmentId, null)
        );

        verifyNoMoreInteractions(assignmentRepo);    }

    @Test
    void addMaterialToAssignment_shouldThrow_whenAssignmentNotFound() {
        Integer assignmentId = 101;
        Material material = new Material(1, "material", "pdf", "category", "https://example.com/material", 101, LocalDateTime.now());

        doThrow(new IllegalArgumentException("Assignment not found"))
                .when(assignmentRepo).ensureAssignmentExists(assignmentId);

        assertThrows(IllegalArgumentException.class, () ->
                assignmentService.addMaterialToAssignment(assignmentId, material)
        );

        verify(assignmentRepo).ensureAssignmentExists(assignmentId);
        verifyNoMoreInteractions(assignmentRepo);
    }

    @Test
    void addMaterialToAssignment_shouldAddMaterial_whenValid() {
        Integer assignmentId = 101;
        Material material = new Material(1, "material", "pdf", "category", "https://example.com/material", 101, LocalDateTime.now());

        doNothing().when(assignmentRepo).ensureAssignmentExists(assignmentId);

        assignmentService.addMaterialToAssignment(assignmentId, material);

        verify(assignmentRepo).ensureAssignmentExists(assignmentId);
        verify(assignmentRepo).addMaterial(assignmentId, material);
    }


    @Test
    void removeMaterialFromAssignment_shouldThrow_whenMaterialIdIsNull() {
        Integer assignmentId = 101;

        assertThrows(NullPointerException.class, () ->
                assignmentService.removeMaterialFromAssignment(assignmentId, null)
        );

        verifyNoMoreInteractions(assignmentRepo);
    }

    @Test
    void removeMaterialFromAssignment_shouldThrow_whenAssignmentNotFound() {
        Integer assignmentId = 101;
        Integer materialId = 55;

        doThrow(new IllegalArgumentException("Assignment not found"))
                .when(assignmentRepo).ensureAssignmentExists(assignmentId);

        assertThrows(IllegalArgumentException.class, () ->
                assignmentService.removeMaterialFromAssignment(assignmentId, materialId)
        );

        verify(assignmentRepo).ensureAssignmentExists(assignmentId);
        verifyNoMoreInteractions(assignmentRepo);
    }

    @Test
    void removeMaterialFromAssignment_shouldRemoveMaterial_whenValid() {
        Integer assignmentId = 101;
        Integer materialId = 55;

        doNothing().when(assignmentRepo).ensureAssignmentExists(assignmentId);

        assignmentService.removeMaterialFromAssignment(assignmentId, materialId);

        verify(assignmentRepo).ensureAssignmentExists(assignmentId);
        verify(assignmentRepo).removeMaterial(assignmentId, materialId);
    }

    @Test
    void getAssignmentsForCourse_shouldReturnAssignments_whenCourseExists() {
        Integer courseId = 101;
        List<Assignment> assignments = List.of(
                new Assignment("Desc1", null, LocalDateTime.now(), 1, courseId),
                new Assignment("Desc2", null, LocalDateTime.now(), 2, courseId)
        );

        doNothing().when(courseRepo).ensureCourseExists(courseId);
        when(assignmentRepo.findAllByCourseId(courseId)).thenReturn(assignments);

        List<Assignment> result = assignmentService.getAssignmentsForCourse(courseId);

        verify(courseRepo).ensureCourseExists(courseId);
        verify(assignmentRepo).findAllByCourseId(courseId);
        assertEquals(assignments, result);
    }

    @Test
    void getAssignmentsForCourse_shouldThrow_whenCourseDoesNotExist() {
        Integer courseId = 101;

        doThrow(new IllegalArgumentException("Course does not exist"))
                .when(courseRepo).ensureCourseExists(courseId);

        assertThrows(IllegalArgumentException.class, () ->
                assignmentService.getAssignmentsForCourse(courseId)
        );

        verify(courseRepo).ensureCourseExists(courseId);
        verifyNoInteractions(assignmentRepo);
    }

}