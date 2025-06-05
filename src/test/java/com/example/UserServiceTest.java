package com.example;

import com.example.Model.*;
import com.example.Repository.UserRepository;
import com.example.Service.UserServiceImpl;
import com.example.Utils.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepo;
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        userRepo = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepo);
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepo.findByEmail("john@example.com")).thenReturn(null);

        User user = userService.registerUser("John", "john123", "john@example.com", "password", Role.STUDENT);

        assertNotNull(user);
        assertTrue(user instanceof Student);
        assertEquals("john123", user.getUserName());
        verify(userRepo).save(user);
    }

    @Test
    void testRegisterUser_EmailAlreadyExists_ThrowsException() {
        when(userRepo.findByEmail("john@example.com"))
                .thenReturn(new Student("John Doe", "john123", "john@example.com", "hashedPassword"));

        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("John", "john123", "john@example.com", "password", Role.STUDENT));
    }

    @Test
    void testLogin_Success() {
        String hashed = StringUtils.applySha256("password");
        User user = new Student("John", "john123", "john@example.com", hashed);
        when(userRepo.findByEmail("john@example.com")).thenReturn(user);

        User result = userService.login("john@example.com", "password");

        assertEquals(user, result);
    }

    @Test
    void testLogin_InvalidCredentials_ThrowsException() {
        when(userRepo.findByEmail("john@example.com")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                userService.login("john@example.com", "wrongpassword"));
    }

    @Test
    void testUpdateUser_Success() {
        Student student = new Student("John", "john123", "john@example.com", "pass");
        when(userRepo.findById(student.getUserId())).thenReturn(student);

        userService.updateUser(student);

        verify(userRepo).save(student);
    }

    @Test
    void testUpdateUser_NotFound_ThrowsException() {
        Student student = new Student("John", "john123", "john@example.com", "pass");
        when(userRepo.findById(student.getUserId())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(student));
    }

    @Test
    void testGetUserById_Success() {
        Student student = new Student("John", "john123", "john@example.com", "pass");
        when(userRepo.findById(student.getUserId())).thenReturn(student);

        User result = userService.getUserById(student.getUserId());

        assertEquals(student, result);
    }

    @Test
    void testGetUserById_NotFound_ThrowsException() {
        when(userRepo.findById("nonexistent")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.getUserById("nonexistent"));
    }

    @Test
    void testDeleteUser_Success() {
        Student student = new Student("John", "john123", "john@example.com", "pass");
        when(userRepo.findById(student.getUserId())).thenReturn(student);

        userService.deleteUser(student.getUserId());

        verify(userRepo).delete(student.getUserId());
    }

    @Test
    void testDeleteUser_NotFound_ThrowsException() {
        when(userRepo.findById("id")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser("id"));
    }

    @Test
    void testGetUsersByRole() {
        Student s1 = new Student("Alice", "alice123", "alice@example.com", "pass1");
        Student s2 = new Student("Bob", "bob456", "bob@example.com", "pass2");
        List<User> users = List.of(s1, s2);

        when(userRepo.findByRole(Role.STUDENT)).thenReturn(users);

        List<User> result = userService.getUsersByRole(Role.STUDENT);

        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof Student);
        assertEquals("Alice", result.get(0).getName());
    }


    @Test
    void testGetUserByEmail_Success() {
        Student student = new Student("John", "john123", "john@example.com", "pass");
        when(userRepo.findByEmail("john@example.com")).thenReturn(student);

        User result = userService.getUserByEmail("john@example.com");

        assertEquals(student, result);
    }

    @Test
    void testGetUserByUserName_Success() {
        Student student = new Student("John", "john123", "john@example.com", "pass");
        when(userRepo.findByUsername("john123")).thenReturn(student);

        User result = userService.getUserByUserName("john123");

        assertEquals(student, result);
    }
}
