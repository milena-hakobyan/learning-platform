package com.example.unit;

import com.example.model.Instructor;
import com.example.model.Student;
import com.example.repository.InstructorRepository;
import com.example.repository.StudentRepository;
import com.example.repository.UserRepository;
import com.example.service.RegistrationService;
import com.example.service.RegistrationServiceImpl;
import com.example.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceUnitTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private StudentRepository studentRepo;
    @Mock
    private InstructorRepository instructorRepo;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    void registerStudent_shouldCreateAndSaveStudent() {
        String username = "student1";
        String email = "student1@example.com";
        String password = "password123";
        String hashedPassword = StringUtils.applySha256(password);

        doNothing().when(userRepo).ensureEmailAndUsernameAvailable(username, email);
        when(studentRepo.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Student result = registrationService.registerStudent(
                username, "Jane", "Doe", email, password
        );

        assertNotNull(result);
        assertEquals(username, result.getUserName());
        assertEquals(email, result.getEmail());
        assertEquals(hashedPassword, result.getPassword());

        verify(userRepo).ensureEmailAndUsernameAvailable(username, email);
        verify(studentRepo).save(result);
        verifyNoInteractions(instructorRepo);
    }

    @Test
    void registerInstructor_shouldCreateAndSaveInstructor() {
        String username = "instructor1";
        String email = "instructor1@example.com";
        String password = "securepass";
        String bio = "Experienced instructor";
        String hashedPassword = StringUtils.applySha256(password);

        doNothing().when(userRepo).ensureEmailAndUsernameAvailable(username, email);
        when(instructorRepo.save(any(Instructor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Instructor result = registrationService.registerInstructor(
                username, "John", "Smith", email, password, bio
        );

        assertNotNull(result);
        assertEquals(username, result.getUserName());
        assertEquals(email, result.getEmail());
        assertEquals(hashedPassword, result.getPassword());
        assertEquals(bio, result.getBio());

        verify(userRepo).ensureEmailAndUsernameAvailable(username, email);
        verify(instructorRepo).save(result);
        verifyNoInteractions(studentRepo);
    }
}
