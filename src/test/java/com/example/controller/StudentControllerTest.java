package com.example.controller;

import com.example.dto.course.CourseResponse;
import com.example.dto.grade.GradeResponse;
import com.example.dto.material.MaterialResponse;
import com.example.dto.student.StudentResponse;
import com.example.dto.submission.CreateSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getStudent_whenFound_shouldReturnStudent() throws Exception {
        StudentResponse student = new StudentResponse();
        student.setId(1L);
        student.setUsername("student1");

        Mockito.when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/api/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("student1"));
    }

    @Test
    void getStudent_whenNotFound_shouldReturn404() throws Exception {
        Mockito.when(studentService.getStudentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/student/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEnrolledCourses_shouldReturnList() throws Exception {
        CourseResponse course = new CourseResponse();
        course.setId(10L);
        course.setTitle("Course 101");

        Mockito.when(studentService.getEnrolledCourses(1L)).thenReturn(List.of(course));

        mockMvc.perform(get("/api/student/1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].title").value("Course 101"));
    }

    @Test
    void getSubmissions_shouldReturnList() throws Exception {
        SubmissionResponse submission = new SubmissionResponse();
        submission.setId(5L);
        submission.setContentLink("My submission");

        Mockito.when(studentService.getSubmissionsByStudentId(1L)).thenReturn(List.of(submission));

        mockMvc.perform(get("/api/student/1/submissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5L))
                .andExpect(jsonPath("$[0].contentLink").value("My submission"));
    }

    @Test
    void getGradesForCourse_shouldReturnList() throws Exception {
        GradeResponse grade = new GradeResponse();
        grade.setId(3L);
        grade.setScore(95.0);

        Mockito.when(studentService.getGradesForCourse(100L, 1L)).thenReturn(List.of(grade));

        mockMvc.perform(get("/api/student/1/grades/course/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].score").value(95));
    }

    @Test
    void getAssignmentGrade_whenFound_shouldReturnGrade() throws Exception {
        GradeResponse grade = new GradeResponse();
        grade.setId(7L);
        grade.setScore(88.0);

        Mockito.when(studentService.getAssignmentGradeForStudent(200L, 1L))
                .thenReturn(Optional.of(grade));

        mockMvc.perform(get("/api/student/1/grades/assignment/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.score").value(88));
    }

    @Test
    void getAssignmentGrade_whenNotFound_shouldReturn404() throws Exception {
        Mockito.when(studentService.getAssignmentGradeForStudent(200L, 1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/student/1/grades/assignment/200"))
                .andExpect(status().isNotFound());
    }

    @Test
    void enrollInCourse_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/student/1/enroll/100"))
                .andExpect(status().isCreated());

        Mockito.verify(studentService).enrollInCourse(1L, 100L);
    }

    @Test
    void dropCourse_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/student/1/drop/100"))
                .andExpect(status().isNoContent());

        Mockito.verify(studentService).dropCourse(1L, 100L);
    }

    @Test
    void browseAvailableCourses_shouldReturnList() throws Exception {
        CourseResponse course = new CourseResponse();
        course.setId(55L);
        course.setTitle("Available Course");

        Mockito.when(studentService.browseAvailableCourses()).thenReturn(List.of(course));

        mockMvc.perform(get("/api/student/courses/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(55L))
                .andExpect(jsonPath("$[0].title").value("Available Course"));
    }

    @Test
    void accessMaterials_shouldReturnList() throws Exception {
        MaterialResponse material = new MaterialResponse();
        material.setId(20L);
        material.setTitle("Lesson Material");

        Mockito.when(studentService.accessMaterials(1L, 10L)).thenReturn(List.of(material));

        mockMvc.perform(get("/api/student/1/materials/lesson/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20L))
                .andExpect(jsonPath("$[0].title").value("Lesson Material"));
    }

    @Test
    void submitAssignment_shouldReturnCreated() throws Exception {
        CreateSubmissionRequest request = new CreateSubmissionRequest();
        request.setContentLink("My submission content");

        mockMvc.perform(post("/api/student/1/submit/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Mockito.verify(studentService).submitAssignment(eq(1L), eq(100L), any(CreateSubmissionRequest.class));
    }
}
