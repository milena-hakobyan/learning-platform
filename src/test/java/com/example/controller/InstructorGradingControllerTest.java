package com.example.controller;

import com.example.dto.grade.GradeSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.model.SubmissionStatus;
import com.example.service.InstructorGradingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorGradingController.class)
public class InstructorGradingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InstructorGradingService gradingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void gradeSubmission_shouldReturnOk() throws Exception {
        GradeSubmissionRequest request = new GradeSubmissionRequest();
        request.setScore(95.0);
        request.setFeedback("Good job");

        // No need to mock void method; just verify called later if needed.

        mockMvc.perform(post("/api/instructors/1/grading/submissions/100/grade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(gradingService).gradeSubmission(eq(1L), eq(100L), any(GradeSubmissionRequest.class));
    }

    @Test
    void getSubmissionsForAssignment_shouldReturnList() throws Exception {
        SubmissionResponse submission = new SubmissionResponse();
        submission.setId(123L);
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setContentLink("My submission content");

        Mockito.when(gradingService.getSubmissionsForAssignment(2L, 200L))
                .thenReturn(List.of(submission));

        mockMvc.perform(get("/api/instructors/2/grading/assignments/200/submissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(123L))
                .andExpect(jsonPath("$[0].status").value("SUBMITTED"))
                .andExpect(jsonPath("$[0].contentLink").value("My submission content"));
    }
}
