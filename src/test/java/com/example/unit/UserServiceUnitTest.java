package com.example.unit;

import com.example.model.Instructor;
import com.example.model.Role;
import com.example.model.Student;
import com.example.model.User;
import com.example.repository.InstructorRepository;
import com.example.repository.StudentRepository;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import com.example.service.UserServiceImpl;
import com.example.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    @Mock
    private UserRepository userRepo;
    @Mock
    private StudentRepository studentRepo;
    @Mock
    private InstructorRepository instructorRepo;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void updateUser_shouldThrow_whenIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                userService.updateUser(null));
    }

    @Test
    void updateUser_shouldThrow_whenUserDoesNotExist() {
        User user = new User(101, "jdoe", "John", "Doe", "jdoe@example.com", "pass123",
                Role.STUDENT, LocalDateTime.now(), true);

        doThrow(new IllegalArgumentException("User not found"))
                .when(userRepo).ensureUserExists(user.getId());

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(user));

        verify(userRepo).ensureUserExists(user.getId());
        verify(userRepo, never()).update(any());
    }


    @Test
    void updateUser_shouldUpdate_whenUserIsValid() {
        User user = new User(101, "jdoe", "John", "Doe", "jdoe@example.com", "pass123",
                Role.STUDENT, LocalDateTime.now(), true);

        doNothing().when(userRepo).ensureUserExists(101);
        when(userRepo.update(user)).thenReturn(user);

        userService.updateUser(user);

        verify(userRepo).ensureUserExists(user.getId());
        verify(userRepo).update(user);

    }

    @Test
    void login_shouldThrow_whenEmailIsNull() {
        assertThrows(NullPointerException.class, () ->
                userService.login(null, "password"));

    }

    @Test
    void login_shouldThrow_whenPassIsNull() {
        assertThrows(NullPointerException.class, () ->
                userService.login("user@gmail.com", null));
    }

    @Test
    void login_shouldThrow_whenEmailNotFound() {
        String email = "example@mail.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.login(email, "password"));

        assertEquals("UserService: user with given email does not exist", exception.getMessage());
        verify(userRepo).findByEmail(email);
        verifyNoMoreInteractions(userRepo, studentRepo, instructorRepo);
    }

    @Test
    void login_shouldThrow_whenWrongPass(){
        String email = "example@yahoo.com";
        String correctPasswordHash = StringUtils.applySha256("correctPassword");
        String wrongPassword = "wrongPassword";

        User user = new User(1, "jdoe", "John", "Doe", email, correctPasswordHash, Role.STUDENT, LocalDateTime.now(), true);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.login(email, wrongPassword)
        );

        assertEquals("UserService: incorrect password", exception.getMessage());
        verify(userRepo).findByEmail(email);
    }

    @Test
    void login_validCredentials() {
        String email = "example@yahoo.com";
        String passwordHash = StringUtils.applySha256("correctPassword");

        User user = new User(1, "jdoe", "John", "Doe", email, passwordHash, Role.STUDENT, LocalDateTime.now(), true);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.login(email, "correctPassword");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepo).findByEmail(email);
    }

    @Test
    void getUserById_shouldThrow_whenIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                userService.getUserById(null)
        );
    }

    @Test
    void getUserById_shouldReturnUser_whenIdIsValid() {
        Integer userId = 101;
        User mockUser = new User(userId, "john_doe", "John", "Doe", "john@example.com", "pass123", Role.STUDENT, LocalDateTime.now(), true);

        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
        verify(userRepo).findById(userId);
    }


    @Test
    void getUserByEmail_shouldThrow_whenEmailIsNull() {
        assertThrows(NullPointerException.class, () ->
                userService.getUserByEmail(null)
        );
    }

    @Test
    void getUserByEmail_shouldReturnUser_whenEmailIsValid() {
        String email = "john@example.com";
        User user = new User(1, "jdoe", "John", "Doe", email, "securePass123", Role.STUDENT, LocalDateTime.of(2024, 12, 1, 10, 0), true);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepo).findByEmail(email);
    }

    @Test
    void getUserByUserName_shouldThrow_whenUsernameIsNull() {
        assertThrows(NullPointerException.class, () ->
                userService.getUserByUserName(null)
        );
    }

    @Test
    void getUserByUserName_shouldReturnUser_whenUsernameIsValid() {
        String username = "jdoe";
        User user = new User(1, username, "John", "Doe", "john@example.com", "securePass123", Role.STUDENT, LocalDateTime.of(2024, 12, 1, 10, 0), true);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByUserName(username);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepo).findByUsername(username);
    }

    @Test
    void getUsersByRole_shouldThrow_whenRoleIsNull() {
        assertThrows(NullPointerException.class, () ->
                userService.getUsersByRole(null)
        );
    }

    @Test
    void getUsersByRole_shouldReturnUsersWithGivenRole() {
        Role role = Role.STUDENT;
        List<User> mockUsers = List.of(
                new User(1, "student1", "Anna", "Lee", "anna@example.com", "pass123", role, LocalDateTime.now(), true),
                new User(2, "student2", "Ben", "Wright", "ben@example.com", "pass456", role, LocalDateTime.now(), true)
        );

        when(userRepo.findAllByRole(role)).thenReturn(mockUsers);

        List<User> result = userService.getUsersByRole(role);

        verify(userRepo).findAllByRole(role);
        assertEquals(mockUsers, result);
    }

    @Test
    void deleteUser_shouldDeleteStudentAndUser_whenStudentExists() {
        Integer userId = 202;
        Student student = mock(Student.class);

        doNothing().when(userRepo).ensureUserExists(userId);
        when(studentRepo.findById(userId)).thenReturn(Optional.of(student));
        when(instructorRepo.findById(userId)).thenReturn(Optional.empty());

        userService.deleteUser(userId);

        verify(studentRepo).delete(userId);
        verify(userRepo).delete(userId);
    }

    @Test
    void deleteUser_shouldDeleteInstructorAndUser_whenInstructorExists() {
        Integer userId = 303;
        Instructor instructor = mock(Instructor.class);

        doNothing().when(userRepo).ensureUserExists(userId);
        when(studentRepo.findById(userId)).thenReturn(Optional.empty());
        when(instructorRepo.findById(userId)).thenReturn(Optional.of(instructor));

        userService.deleteUser(userId);

        verify(instructorRepo).delete(userId);
        verify(userRepo).delete(userId);
    }

    @Test
    void deleteUser_shouldThrow_whenUserDoesNotExist() {
        Integer userId = 404;

        doThrow(new IllegalArgumentException("User not found"))
                .when(userRepo).ensureUserExists(userId);

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(userId));

        verify(userRepo).ensureUserExists(userId);
        verifyNoMoreInteractions(userRepo, studentRepo, instructorRepo);
    }


    @Test
    void deactivateUser() {
        Integer userID = 101;

        doNothing().when(userRepo).ensureUserExists(userID);
        doNothing().when(userRepo).deactivateUser(userID);

        userService.deactivateUser(userID);

        verify(userRepo).ensureUserExists(userID);
        verify(userRepo).deactivateUser(userID);
    }

    @Test
    void deactivateUser_shouldThrow_whenUserDoesNotExist() {
        Integer userID = 101;

        doThrow(new IllegalArgumentException("User not found")).when(userRepo).ensureUserExists(userID);

        assertThrows(IllegalArgumentException.class, () -> userService.deactivateUser(userID));

        verify(userRepo, never()).deactivateUser(anyInt());
    }
}