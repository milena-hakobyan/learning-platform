package com.example.service;

import com.example.model.Role;
import com.example.model.Student;
import com.example.model.User;
import com.example.repository.InstructorRepository;
import com.example.repository.StudentRepository;
import com.example.repository.UserRepository;
import com.example.utils.DatabaseConnection;
import com.example.utils.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
class UserServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepo;
    @Autowired private StudentRepository studentRepo;
    @Autowired private InstructorRepository instructorRepo;
    @Autowired private DatabaseConnection db;

    private User testUser;
    private final String password = "secure123";

    @BeforeEach
    void setup() {
        testUser = new Student(null, "john_doe", "John", "Doe",
                "john@example.com", StringUtils.applySha256(password),
                LocalDateTime.now(), true, 0.0, 0, 0);
        testUser = studentRepo.save((Student) testUser);
    }

    @AfterEach
    void cleanup() {
        db.execute("TRUNCATE TABLE students, instructors, users RESTART IDENTITY CASCADE");
    }

    @Test
    void login_shouldSucceed_withCorrectCredentials() {
        Optional<User> result = userService.login(testUser.getEmail(), password);

        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void login_shouldFail_withIncorrectPassword() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.login(testUser.getEmail(), "wrongpass"));
        assertEquals("UserService: incorrect password", ex.getMessage());
    }

    @Test
    void login_shouldFail_ifEmailNotFound() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.login("missing@example.com", password));
        assertEquals("UserService: user with given email does not exist", ex.getMessage());
    }

    @Test
    void updateUser_shouldModifyUserDetails() {
        testUser.setFirstName("Updated");
        userService.updateUser(testUser);

        User updated = userRepo.findById(testUser.getId()).orElseThrow();
        assertEquals("Updated", updated.getFirstName());
    }

    @Test
    void getUserById_shouldReturnCorrectUser() {
        Optional<User> result = userService.getUserById(testUser.getId());
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
    }

    @Test
    void getUserByEmail_shouldReturnCorrectUser() {
        Optional<User> result = userService.getUserByEmail(testUser.getEmail());
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void getUserByUsername_shouldReturnCorrectUser() {
        Optional<User> result = userService.getUserByUserName(testUser.getUserName());
        assertTrue(result.isPresent());
        assertEquals(testUser.getUserName(), result.get().getUserName());
    }

    @Test
    void getUsersByRole_shouldReturnCorrectList() {
        List<User> students = userService.getUsersByRole(Role.STUDENT);
        assertFalse(students.isEmpty());
        assertTrue(students.stream().allMatch(u -> u.getRole() == Role.STUDENT));
    }

    @Test
    void deleteUser_shouldRemoveUserAndStudent() {
        userService.deleteUser(testUser.getId());

        assertTrue(userRepo.findById(testUser.getId()).isEmpty());
        assertTrue(studentRepo.findById(testUser.getId()).isEmpty());
    }

    @Test
    void deactivateUser_shouldSetActiveFalse() {
        userService.deactivateUser(testUser.getId());

        User user = userRepo.findById(testUser.getId()).orElseThrow();
        assertFalse(user.isActive());
    }
}
