package com.example.controller;

import com.example.dto.grade.GradeSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.model.*;
import com.example.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@Transactional
public class InstructorGradingControllerIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaInstructorRepository instructorRepository;

    @Autowired
    private JpaStudentRepository studentRepository;

    @Autowired
    private JpaAssignmentRepository assignmentRepository;

    @Autowired
    private JpaSubmissionRepository submissionRepository;

    @Autowired
    private JpaCourseRepository courseRepository;

    private User instructorUser;
    private Instructor instructor;
    private Assignment assignment;
    private Submission submission;

    @BeforeEach
    void setUp() {
        submissionRepository.deleteAll();
        assignmentRepository.deleteAll();
        courseRepository.deleteAll();
        instructorRepository.deleteAll();
        userRepository.deleteAll();
        studentRepository.deleteAll();

        instructorUser = new User();
        instructorUser.setUsername("instructor1");
        instructorUser.setPassword("password123");
        instructorUser.setEmail("instructor1@example.com");
        instructorUser.setRole(Role.INSTRUCTOR);
        instructorUser.setActive(true);
        instructorUser.setLastLogin(LocalDateTime.now());
        instructorUser = userRepository.saveAndFlush(instructorUser);

        instructor = new Instructor();
        instructor.setUser(instructorUser);
        instructor.setBio("Experienced instructor");
        instructor.setVerified(true);
        instructor = instructorRepository.saveAndFlush(instructor);

        Course course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Course description");
        course.setInstructor(instructor);
        course = courseRepository.saveAndFlush(course);

        assignment = new Assignment();
        assignment.setTitle("Test Assignment");
        assignment.setCourse(course);
        assignment.setDueDate(LocalDateTime.now().plusDays(7));
        assignment.setMaxScore(100.0);
        assignment = assignmentRepository.saveAndFlush(assignment);

        User studentUser = new User();
        studentUser.setUsername("student1");
        studentUser.setPassword("password123");
        studentUser.setEmail("student1@example.com");
        studentUser.setRole(Role.STUDENT);
        studentUser.setActive(true);
        studentUser.setLastLogin(LocalDateTime.now());
        studentUser = userRepository.saveAndFlush(studentUser);

        Student student = new Student();
        student.setUser(studentUser);
        student = studentRepository.saveAndFlush(student);

        submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContentLink("My submission");
        submission.setSubmittedAt(LocalDateTime.now());
        submission = submissionRepository.saveAndFlush(submission);
    }


    @Test
    void gradeSubmission_shouldReturnOk_whenValid() throws Exception {
        GradeSubmissionRequest request = new GradeSubmissionRequest();
        request.setScore(95.0);
        request.setFeedback("Great job!");

        mockMvc.perform(post("/api/instructors/{instructorId}/grading/submissions/{submissionId}/grade", instructorUser.getId(), submission.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void gradeSubmission_shouldReturn400_whenInvalidScore() throws Exception {
        GradeSubmissionRequest request = new GradeSubmissionRequest();
        request.setScore(150.0); // invalid score, assuming max 100
        request.setFeedback("Invalid score test");

        mockMvc.perform(post("/api/instructors/{instructorId}/grading/submissions/{submissionId}/grade", instructorUser.getId(), submission.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSubmissionsForAssignment_shouldReturnPagedSubmissions() throws Exception {
        mockMvc.perform(get("/api/instructors/{instructorId}/grading/assignments/{assignmentId}/submissions", instructorUser.getId(), assignment.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].contentLink", is("My submission")));
    }

    @Test
    void getSubmissionsForAssignment_shouldReturn404_whenAssignmentNotFound() throws Exception {
        mockMvc.perform(get("/api/instructors/{instructorId}/grading/assignments/{assignmentId}/submissions", instructorUser.getId(), 9999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
