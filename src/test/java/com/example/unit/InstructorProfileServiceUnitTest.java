package com.example.unit;

import com.example.model.Instructor;
import com.example.service.InstructorProfileServiceImpl;
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
class InstructorProfileServiceUnitTest {

    @Mock
    private InstructorRepository instructorRepo;

    @InjectMocks
    private InstructorProfileServiceImpl profileService;

    @Test
    void getAllInstructors_shouldReturnAllInstructorsFromRepository() {
        List<Instructor> instructors = List.of(
                new Instructor(1, "jsmith", "John", "Smith", "jsmith@example.com", "pass123",
                        LocalDateTime.now(), true, "Java Developer", 5, 4.7, true),
                new Instructor(2, "djohnson", "Diana", "Johnson", "djohnson@example.com", "pass456",
                        LocalDateTime.now(), true, "Python Enthusiast", 3, 4.5, false)
        );

        when(instructorRepo.findAll()).thenReturn(instructors);

        List<Instructor> result = profileService.getAllInstructors();

        verify(instructorRepo).findAll();
        assertEquals(instructors, result);
    }

    @Test
    void getInstructorById_shouldReturnInstructor_whenExists() {
        Integer instructorId = 101;
        Instructor mockInstructor = new Instructor(
                instructorId, "jsmith", "John", "Smith", "jsmith@example.com", "pass123",
                LocalDateTime.now(), true, "Java Developer", 5, 4.7, true
        );

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        when(instructorRepo.findById(instructorId)).thenReturn(Optional.of(mockInstructor));

        Optional<Instructor> result = profileService.getInstructorById(instructorId);

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(instructorRepo).findById(instructorId);
        assertTrue(result.isPresent());
        assertEquals(mockInstructor, result.get());
    }
}
