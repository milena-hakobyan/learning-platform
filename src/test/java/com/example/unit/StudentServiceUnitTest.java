package com.example.unit;

import com.example.model.*;
import com.example.repository.*;
import com.example.service.CourseEnrollmentService;
import com.example.service.CourseManagementService;
import com.example.service.StudentService;
import com.example.service.StudentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceUnitTest {

    @Mock
    private CourseManagementService courseManagementService;
    @Mock
    private CourseEnrollmentService enrollmentService;
    @Mock
    private StudentRepository studentRepo;
    @Mock
    private SubmissionRepository submissionRepo;
    @Mock
    private LessonRepository lessonRepo;
    @Mock
    private GradeRepository gradeRepo;
    @Mock
    private AssignmentRepository assignmentRepo;
    @Mock
    private ActivityLogRepository activityLogRepo;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void getStudentById_shouldReturnStudent_whenExists() {
        Integer studentId = 1;
        Student student = mock(Student.class);

        doNothing().when(studentRepo).ensureStudentExists(studentId);
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));

        Optional<Student> result = studentService.getStudentById(studentId);

        assertTrue(result.isPresent());
        assertEquals(student, result.get());
    }

    @Test
    void getEnrolledCourses_shouldReturnCourses() {
        Integer studentId = 1;
        List<Course> courses = List.of(mock(Course.class));

        doNothing().when(studentRepo).ensureStudentExists(studentId);
        when(studentRepo.findAllEnrolledCourses(studentId)).thenReturn(courses);

        assertEquals(courses, studentService.getEnrolledCourses(studentId));
    }

    @Test
    void getSubmissionsByStudentId_shouldReturnSubmissions() {
        Integer studentId = 1;
        List<Submission> submissions = List.of(mock(Submission.class));

        doNothing().when(studentRepo).ensureStudentExists(studentId);
        when(submissionRepo.findAllByStudentId(studentId)).thenReturn(submissions);

        assertEquals(submissions, studentService.getSubmissionsByStudentId(studentId));
    }

    @Test
    void getGradesForCourse_shouldReturnMap() {
        Integer studentId = 1, courseId = 10;
        Map<Assignment, Grade> gradeMap = Map.of(mock(Assignment.class), mock(Grade.class));

        doNothing().when(studentRepo).ensureStudentExists(studentId);
        doNothing().when(enrollmentService).ensureStudentEnrollment(courseId, studentId);
        when(gradeRepo.findGradesByStudentIdForCourse(studentId, courseId)).thenReturn(gradeMap);

        assertEquals(gradeMap, studentService.getGradesForCourse(courseId, studentId));
    }

    @Test
    void getAssignmentGradeForStudent_shouldReturnGrade() {
        Integer studentId = 1, assignmentId = 5;
        Grade grade = mock(Grade.class);

        doNothing().when(assignmentRepo).ensureAssignmentExists(assignmentId);
        doNothing().when(studentRepo).ensureStudentExists(studentId);
        when(gradeRepo.findByAssignmentIdAndStudentId(assignmentId, studentId)).thenReturn(Optional.of(grade));

        Optional<Grade> result = studentService.getAssignmentGradeForStudent(assignmentId, studentId);

        assertTrue(result.isPresent());
        assertEquals(grade, result.get());
    }

    @Test
    void enrollInCourse_shouldSaveActivity() {
        Integer studentId = 1, courseId = 5;
        Course course = new Course(courseId, "Java", "desc", "cat", "url", 1);

        doNothing().when(studentRepo).ensureStudentExists(studentId);
        when(courseManagementService.getCourseById(courseId)).thenReturn(Optional.of(course));

        studentService.enrollInCourse(studentId, courseId);

        verify(enrollmentService).enrollStudent(courseId, studentId);
        verify(activityLogRepo).save(any(ActivityLog.class));
    }

    @Test
    void dropCourse_shouldSaveActivity() {
        Integer studentId = 1, courseId = 5;
        Course course = new Course(courseId, "Java", "desc", "cat", "url", 1);

        doNothing().when(studentRepo).ensureStudentExists(studentId);
        when(courseManagementService.getCourseById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(enrollmentService).ensureStudentEnrollment(studentId, courseId);

        studentService.dropCourse(studentId, courseId);

        verify(enrollmentService).unenrollStudent(courseId, studentId);
        verify(activityLogRepo).save(any(ActivityLog.class));
    }

    @Test
    void browseAvailableCourses_shouldReturnList() {
        List<Course> courses = List.of(new Course(1, "Java", "desc", "cat", "url", 1));
        when(courseManagementService.getAllCourses()).thenReturn(courses);

        assertEquals(courses, studentService.browseAvailableCourses());
    }

    @Test
    void accessMaterials_shouldReturnListAndLogActivity() {
        Integer studentId = 1, lessonId = 5;
        List<Material> materials = List.of(mock(Material.class));

        doNothing().when(studentRepo).ensureStudentExists(studentId);
        doNothing().when(lessonRepo).ensureLessonExists(lessonId);
        doNothing().when(lessonRepo).ensureStudentAccessToLesson(studentId, lessonId);
        when(lessonRepo.findAllMaterialsByLessonId(lessonId)).thenReturn(materials);

        List<Material> result = studentService.accessMaterials(studentId, lessonId);

        assertEquals(materials, result);
        verify(activityLogRepo).save(any(ActivityLog.class));
    }

    @Test
    void submitAssignment_shouldThrow_whenStudentNotFound() {
        when(studentRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                studentService.submitAssignment(100, 1, 10, "link"));

        verifyNoMoreInteractions(assignmentRepo);
    }

    @Test
    void submitAssignment_shouldThrow_whenAssignmentNotFound() {
        Student student = new Student(
                1, "user", "f", "l", "e", "p",
                LocalDateTime.now(), true, 0.0, 0, 0
        );

        when(studentRepo.findById(1)).thenReturn(Optional.of(student));
        when(assignmentRepo.findById(10)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                studentService.submitAssignment(100, 1, 10, "link"));
    }


    @Test
    void submitAssignment_shouldThrow_whenCourseNotFound() {
        Student student = new Student(
                1, "user", "f", "l", "e", "p",
                LocalDateTime.now(), true, 0.0, 0, 0
        );
        Assignment assignment = new Assignment("Title", "desc", LocalDateTime.now(), 100, 5);
        assignment.setId(10);

        when(studentRepo.findById(1)).thenReturn(Optional.of(student));
        when(assignmentRepo.findById(10)).thenReturn(Optional.of(assignment));
        when(courseManagementService.getCourseById(5)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                studentService.submitAssignment(100, 1, 10, "link"));
    }

    @Test
    void submitAssignment_shouldThrow_whenAlreadySubmitted() {
        Student student = new Student(
                1, "user", "f", "l", "e", "p",
                LocalDateTime.now(), true, 0.0, 0, 0
        );        Assignment assignment = new Assignment("Title", "desc", LocalDateTime.now(), 100, 5);
        assignment.setId(10);
        Course course = new Course(5, "title", "desc", "cat", "url", 1);

        when(studentRepo.findById(1)).thenReturn(Optional.of(student));
        when(assignmentRepo.findById(10)).thenReturn(Optional.of(assignment));
        when(courseManagementService.getCourseById(5)).thenReturn(Optional.of(course));
        doNothing().when(enrollmentService).ensureStudentEnrollment(1, 5);
        when(submissionRepo.findByAssignmentIdAndStudentId(10, 1)).thenReturn(Optional.of(new Submission(1, 10, "link", LocalDateTime.now())));

        assertThrows(IllegalStateException.class, () ->
                studentService.submitAssignment(100, 1, 10, "link"));
    }

    @Test
    void submitAssignment_shouldSave_whenAllValid() {
        Integer studentId = 1, assignmentId = 10, courseId = 5;
        Student student = new Student(
                1, "user", "f", "l", "e", "p",
                LocalDateTime.now(), true, 0.0, 0, 0
        );        Assignment assignment = new Assignment("Title", "desc", LocalDateTime.now(), 100, courseId);
        assignment.setId(assignmentId);
        Course course = new Course(courseId, "title", "desc", "cat", "url", 1);

        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(assignmentRepo.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(courseManagementService.getCourseById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(enrollmentService).ensureStudentEnrollment(studentId, courseId);
        when(submissionRepo.findByAssignmentIdAndStudentId(assignmentId, studentId)).thenReturn(Optional.empty());

        studentService.submitAssignment(100, studentId, assignmentId, "my-link");

        verify(submissionRepo).save(argThat(s ->
                s.getSubmissionId().equals(100) &&
                        s.getStudentId().equals(studentId) &&
                        s.getAssignmentId().equals(assignmentId) &&
                        s.getContentLink().equals("my-link")
        ));

        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(studentId) &&
                        log.getAction().contains("Submitted assignment: Title")
        ));
    }
}
