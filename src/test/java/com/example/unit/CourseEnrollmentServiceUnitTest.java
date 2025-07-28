package com.example.unit;

import com.example.model.Student;
import com.example.service.CourseEnrollmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseEnrollmentServiceUnitTest {

    @Mock
    CourseRepository courseRepo;

    @InjectMocks
    CourseEnrollmentServiceImpl courseService;

    @Test
    void getEnrolledStudents_shouldCallRepoMethods_andReturnStudents() {
        Integer courseId = 42;
        Student s1 = new Student(
                1, "alice123", "Alice", "Johnson", "alice@example.com", "securepass",
                LocalDateTime.now(), true, 75.0, 3, 1
        );
        List<Student> mockStudents = List.of(s1);

        doNothing().when(courseRepo).ensureCourseExists(courseId);
        when(courseRepo.findEnrolledStudents(courseId)).thenReturn(mockStudents);

        List<Student> result = courseService.getEnrolledStudents(courseId);

        verify(courseRepo).ensureCourseExists(courseId);
        verify(courseRepo).findEnrolledStudents(courseId);

        assertEquals(mockStudents, result);
    }

    @Test
    void enrollStudent_shouldCallRepositoryWithCorrectArguments() {
        Integer courseId = 101;
        Integer studentId = 202;

        courseService.enrollStudent(courseId, studentId);

        verify(courseRepo, times(1)).enrollStudent(courseId, studentId);
    }

    @Test
    void unenrollStudent_shouldCallRepositoryWithCorrectArguments() {
        Integer courseId = 303;
        Integer studentId = 404;

        courseService.unenrollStudent(courseId, studentId);

        verify(courseRepo, times(1)).unenrollStudent(courseId, studentId);
    }

    @Test
    void ensureStudentEnrollment_shouldDoNothing_whenStudentIsEnrolled(){
        Integer courseId = 303;
        Integer studentId = 404;

        when(courseRepo.isStudentEnrolled(studentId, courseId)).thenReturn(true);

        assertDoesNotThrow(() -> courseService.ensureStudentEnrollment(studentId, courseId));
    }

    @Test
    void ensureStudentEnrollment_shouldThrowException_whenStudentIsNotEnrolled(){
        Integer courseId = 505;
        Integer studentId = 606;

        when(courseRepo.isStudentEnrolled(studentId, courseId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> courseService.ensureStudentEnrollment(studentId, courseId)
        );

        assertEquals("Student is not enrolled in the course", exception.getMessage());
    }


}