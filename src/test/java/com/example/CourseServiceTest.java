package com.example;

import com.example.Model.*;
import com.example.Repository.AssignmentRepository;
import com.example.Repository.CourseRepository;
import com.example.Repository.SubmissionRepository;
import com.example.Service.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    private CourseRepository courseRepo;
    private AssignmentRepository assignmentRepo;
    private SubmissionRepository submissionRepo;
    private CourseServiceImpl courseService;

    private Course course;
    private Assignment assignment;
    private Lesson lesson;
    private Student student;

    @BeforeEach
    void setUp() {
        courseRepo = mock(CourseRepository.class);
        assignmentRepo = mock(AssignmentRepository.class);
        submissionRepo = mock(SubmissionRepository.class);
        courseService = new CourseServiceImpl(courseRepo, assignmentRepo, submissionRepo);

        course = new Course("CS101", "Intro to CS", "Basics", "CS Core", "instructor1");
        assignment = new Assignment("A1", "HW1", "CS101", LocalDateTime.of(2025, 12, 1, 23, 59), 100);
        lesson = new Lesson("L1", "Lesson 1");
        student = new Student("student1", "johnsmith", "john@example.com", "password");
    }

    @Test
    void testCreateCourse_Success() {
        when(courseRepo.findById("CS101")).thenReturn(null);
        when(courseRepo.findByTitle("Intro to CS")).thenReturn(null);

        courseService.createCourse(course);

        verify(courseRepo).save(course);
    }

    @Test
    void testCreateCourse_AlreadyExistsById() {
        when(courseRepo.findById("CS101")).thenReturn(course);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(course));
        assertEquals("Course with id 'CS101' already exists.", ex.getMessage());
    }

    @Test
    void testUpdateCourse_Success() {
        when(courseRepo.findById("CS101")).thenReturn(course);

        courseService.updateCourse(course);

        verify(courseRepo).save(course);
    }

    @Test
    void testDeleteCourse_DeletesAssignmentsAndSubmissions() {
        // Create assignment and submission with fixed IDs
        Assignment assignment = new Assignment("A1", "HW1", "CS101", LocalDateTime.now(), 100);
        Student student = new Student("student1", "John", "john@example.com", "pass");

        Submission submission = new Submission(student, assignment, "answer", LocalDateTime.now());
        submission.setSubmissionId("S1"); // Manually assign submission ID

        // Mock repository behavior using those exact IDs
        when(assignmentRepo.findByCourseId("CS101")).thenReturn(List.of(assignment));
        when(submissionRepo.findByAssignmentId(assignment.getAssignmentId())).thenReturn(List.of(submission));

        // Act
        courseService.deleteCourse("CS101");

        // Verify expected deletions
        verify(submissionRepo).delete(submission.getSubmissionId());
        verify(assignmentRepo).delete(assignment.getAssignmentId());
        verify(courseRepo).delete("CS101");
    }



    @Test
    void testAddAssignmentToCourse_Success() {
        when(courseRepo.findById("CS101")).thenReturn(course);

        courseService.addAssignmentToCourse("CS101", assignment);

        assertTrue(course.getAssignments().contains(assignment));
        verify(assignmentRepo).save(assignment);
    }

    @Test
    void testRemoveAssignmentFromCourse_Success() {
        assignment.setCourseId(course.getCourseId());
        course.addAssignment(assignment);

        when(courseRepo.findById("CS101")).thenReturn(course);
        when(assignmentRepo.findById("A1")).thenReturn(assignment);
        when(submissionRepo.findByAssignmentId("A1")).thenReturn(List.of());

        courseService.removeAssignmentFromCourse("CS101", "A1");

        assertFalse(course.getAssignments().contains(assignment));
        verify(assignmentRepo).delete(assignment.getAssignmentId());
    }


    @Test
    void testAddLessonToCourse_Success() {
        when(courseRepo.findById("CS101")).thenReturn(course);

        courseService.addLessonToCourse("CS101", lesson);

        assertTrue(course.getLessons().contains(lesson));
        verify(courseRepo).save(course);
    }

    @Test
    void testRemoveLessonFromCourse_Success() {
        course.addLesson(lesson);
        when(courseRepo.findById("CS101")).thenReturn(course);

        courseService.removeLessonFromCourse("CS101", lesson.getLessonId());

        assertFalse(course.getLessons().contains(lesson));
    }

    @Test
    void testEnrollStudent_Success() {
        when(courseRepo.findById("CS101")).thenReturn(course);

        courseService.enrollStudent("CS101", student);

        assertTrue(course.getEnrolledStudents().contains(student));
        verify(courseRepo).save(course);
    }

    @Test
    void testGetCoursesByInstructor() {
        when(courseRepo.findByInstructor("instructor1")).thenReturn(List.of(course));

        List<Course> result = courseService.getCoursesByInstructor("instructor1");

        assertEquals(1, result.size());
        assertEquals(course, result.get(0));
    }
}
