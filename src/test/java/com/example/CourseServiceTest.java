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
import java.util.Optional;

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
        // given
        when(courseRepo.findById("CS101")).thenReturn(null);
        when(courseRepo.findByTitle("Intro to CS")).thenReturn(null);

        // when
        courseService.createCourse(course);

        // then
        verify(courseRepo).save(course);
    }

    @Test
    void testCreateCourse_AlreadyExistsById() {
        // given
        when(courseRepo.findById("CS101")).thenReturn(Optional.of(course));

        // when & then
        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(course));
        assertEquals("Course with id 'CS101' already exists.", ex.getMessage());
    }

    @Test
    void testUpdateCourse_Success() {
        // given
        when(courseRepo.findById("CS101")).thenReturn(Optional.of(course));

        // when
        courseService.updateCourse(course);

        // then
        verify(courseRepo).save(course);
    }

    @Test
    void testDeleteCourse_DeletesAssignmentsAndSubmissions() {
        // given
        Assignment assignment = new Assignment("A1", "HW1", "CS101", LocalDateTime.now(), 100);
        Student student = new Student("student1", "John", "john@example.com", "pass");
        Submission submission = new Submission(student, assignment, "answer", LocalDateTime.now());
        submission.setSubmissionId("S1");

        when(assignmentRepo.findByCourseId("CS101")).thenReturn(List.of(assignment));
        when(submissionRepo.findByAssignmentId("A1")).thenReturn(List.of(submission));
        when(courseRepo.findById("CS101")).thenReturn(Optional.of(course))  // needed if service fetches it
                .thenReturn(Optional.empty()); // simulate deletion

        // when
        courseService.deleteCourse("CS101");

        // then
        verify(submissionRepo).delete("S1");
        verify(assignmentRepo).delete("A1");
        verify(courseRepo).delete("CS101");

        Optional<Course> deleted = courseRepo.findById("CS101");
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testAddAssignmentToCourse_Success() {
        // given
        when(courseRepo.findById("CS101")).thenReturn(Optional.of(course));

        // when
        courseService.addAssignmentToCourse("CS101", assignment);

        // then
        assertTrue(course.getAssignments().contains(assignment));
        verify(assignmentRepo).save(assignment);
    }

    @Test
    void testRemoveAssignmentFromCourse_Success() {
        // given
        assignment.setCourseId(course.getCourseId());
        course.addAssignment(assignment);
        when(courseRepo.findById("CS101")).thenReturn(Optional.of(course));
        when(assignmentRepo.findById("A1")).thenReturn(Optional.of(assignment));
        when(submissionRepo.findByAssignmentId("A1")).thenReturn(List.of());

        // when
        courseService.removeAssignmentFromCourse("CS101", "A1");

        // then
        assertFalse(course.getAssignments().contains(assignment));
        verify(assignmentRepo).delete("A1");
    }

    @Test
    void testAddLessonToCourse_Success() {
        // given
        when(courseRepo.findById("CS101")).thenReturn(Optional.of(course));

        // when
        courseService.addLessonToCourse("CS101", lesson);

        // then
        assertTrue(course.getLessons().contains(lesson));
        verify(courseRepo).save(course);
    }

    @Test
    void testRemoveLessonFromCourse_Success() {
        // given
        course.addLesson(lesson);
        when(courseRepo.findById("CS101")).thenReturn(Optional.of(course));

        // when
        courseService.removeLessonFromCourse("CS101", lesson.getLessonId());

        // then
        assertFalse(course.getLessons().contains(lesson));
    }

    @Test
    void testEnrollStudent_Success() {
        // given
        when(courseRepo.findById("CS101")).thenReturn(Optional.of(course));

        // when
        courseService.enrollStudent("CS101", student);

        // then
        assertTrue(course.getEnrolledStudents().contains(student));
        verify(courseRepo).save(course);
    }

    @Test
    void testGetCoursesByInstructor() {
        // given
        when(courseRepo.findByInstructor("instructor1")).thenReturn(List.of(course));

        // when
        List<Course> result = courseService.getCoursesByInstructor("instructor1");

        // then
        assertEquals(1, result.size());
        assertEquals(course, result.get(0));
    }

}
