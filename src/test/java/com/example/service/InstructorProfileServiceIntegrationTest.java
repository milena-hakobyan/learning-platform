package com.example.service;

import com.example.model.Instructor;
import com.example.repository.InstructorRepository;
import com.example.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
class InstructorProfileServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private InstructorProfileService profileService;

    @Autowired
    private InstructorRepository instructorRepo;

    @Autowired
    private DatabaseConnection dbConnection;

    @AfterEach
    void cleanUp() {
        dbConnection.execute("TRUNCATE TABLE instructors, users RESTART IDENTITY CASCADE");
    }

    @Test
    void getAllInstructors_shouldReturnSavedInstructors() {
        Instructor instructor1 = new Instructor(
                null, "janedoe", "Jane", "Doe", "jane@example.com", "password",
                LocalDateTime.now(), true, "Java expert", 5, 4.9, true
        );
        Instructor instructor2 = new Instructor(
                null, "bobsmith", "Bob", "Smith", "bob@example.com", "password123",
                LocalDateTime.now(), true, "Python guru", 8, 4.7, true
        );

        instructorRepo.save(instructor1);
        instructorRepo.save(instructor2);

        List<Instructor> instructors = profileService.getAllInstructors();

        assertEquals(2, instructors.size());
        assertTrue(instructors.stream().anyMatch(i -> i.getUserName().equals("janedoe")));
        assertTrue(instructors.stream().anyMatch(i -> i.getUserName().equals("bobsmith")));
    }

    @Test
    void getInstructorById_shouldReturnInstructor_whenExists() {
        Instructor instructor = new Instructor(
                null, "aliceblue", "Alice", "Blue", "alice@example.com", "pass321",
                LocalDateTime.now(), true, "Spring instructor", 4, 4.5, false
        );
        instructor = instructorRepo.save(instructor);

        Optional<Instructor> found = profileService.getInstructorById(instructor.getId());

        assertTrue(found.isPresent());
        assertEquals("aliceblue", found.get().getUserName());
    }

    @Test
    void getInstructorById_shouldThrow_whenNotExists() {
        int nonexistentId = 999;

        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            profileService.getInstructorById(nonexistentId);
        });

        assertTrue(ex.getMessage().equals("User not found"));
    }

}
