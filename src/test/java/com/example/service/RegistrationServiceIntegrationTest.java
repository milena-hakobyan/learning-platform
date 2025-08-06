package com.example.service;

import com.example.model.Instructor;
import com.example.model.Student;
import com.example.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
class RegistrationServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private InstructorRepository instructorRepo;

    @Autowired
    private DatabaseConnection dbConnection;

    @AfterEach
    void cleanUp() {
        dbConnection.execute("TRUNCATE TABLE students, instructors, users RESTART IDENTITY CASCADE");
    }

    private void printUsers() {
        System.out.println("Current users in DB:");
        var users = dbConnection.findMany("SELECT user_id FROM students;", rs -> {
            var list = new java.util.ArrayList<String>();
            try {
                while (rs.next()) {
                    list.add(rs.getString("user_id"));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return list;
        });
        users.forEach(System.out::println);
    }

    @Test
    void registerStudent_shouldSucceed() {
        Student student = registrationService.registerStudent(
                "milena", "Milena", "Hakobyan", "milena@example.com", "securePassword123"
        );

        assertNotNull(student.getId());
        assertEquals("milena", student.getUserName());
        assertEquals("Milena", student.getFirstName());

        assertTrue(userRepo.findById(student.getId()).isPresent());
        assertTrue(studentRepo.findById(student.getId()).isPresent());
    }

    @Test
    void registerInstructor_shouldSucceed() {
        Instructor instructor = registrationService.registerInstructor(
                "johnDoe", "John", "Doe", "john.doe@example.com", "anotherSecurePass", "Experienced Java instructor"
        );

        assertNotNull(instructor.getId());
        assertEquals("johnDoe", instructor.getUserName());
        assertEquals("John", instructor.getFirstName());
        assertEquals("Experienced Java instructor", instructor.getBio());

        assertTrue(userRepo.findById(instructor.getId()).isPresent());
        assertTrue(instructorRepo.findById(instructor.getId()).isPresent());
    }

    @Test
    void registerStudent_withDuplicateUsername_shouldThrow() {
        registrationService.registerStudent(
                "existingUser", "First", "User", "first.user@example.com", "password"
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            registrationService.registerStudent(
                    "existingUser", "Second", "User", "second.user@example.com", "password"
            );
        });

        assertTrue(ex.getMessage().toLowerCase().contains("username"));
    }

    @Test
    void registerInstructor_withDuplicateEmail_shouldThrow() {
        registrationService.registerInstructor(
                "uniqueUser", "Unique", "User", "unique.email@example.com", "password", "Bio"
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            registrationService.registerInstructor(
                    "anotherUser", "Another", "User", "unique.email@example.com", "password", "Bio"
            );
        });

        assertTrue(ex.getMessage().toLowerCase().contains("email"));
    }

    @Test
    void registerStudent_withNullUsername_shouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            registrationService.registerStudent(
                    null, "Null", "Username", "null.username@example.com", "password"
            );
        });

        assertTrue(ex.getMessage().toLowerCase().contains("username"));
    }

    @Test
    void registerInstructor_withNullUsername_shouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            registrationService.registerInstructor(
                    null, "Null", "Instructor", "null.instructor@example.com", "password", "Bio text"
            );
        });

        assertTrue(ex.getMessage().toLowerCase().contains("username"));
    }
}