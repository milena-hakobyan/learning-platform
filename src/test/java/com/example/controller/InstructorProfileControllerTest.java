package com.example.controller;

import com.example.dto.instructor.InstructorResponse;
import com.example.service.InstructorProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorProfileController.class)
public class InstructorProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InstructorProfileService profileService;

    @Test
    void getAllInstructors_shouldReturnList() throws Exception {
        InstructorResponse instructor = new InstructorResponse();
        instructor.setId(1L);
        instructor.setFirstName("John");
        instructor.setLastName("Doe");

        Mockito.when(profileService.getAllInstructors()).thenReturn(List.of(instructor));

        mockMvc.perform(get("/api/instructors")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    void getInstructorById_whenFound_shouldReturnInstructor() throws Exception {
        InstructorResponse instructor = new InstructorResponse();
        instructor.setId(2L);
        instructor.setFirstName("Jane");
        instructor.setLastName("Smith");


        Mockito.when(profileService.getInstructorById(eq(2L))).thenReturn(Optional.of(instructor));

        mockMvc.perform(get("/api/instructors/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void getInstructorById_whenNotFound_shouldReturn404() throws Exception {
        Mockito.when(profileService.getInstructorById(eq(999L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/instructors/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
