package com.example.controller;

import com.example.dto.user.UserResponse;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_success_shouldReturnUser() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(1L);
        user.setEmail("login@example.com");

        Mockito.when(userService.login("login@example.com", "password")).thenReturn(user);

        mockMvc.perform(post("/api/auth/login")
                        .param("email", "login@example.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("login@example.com"));
    }

    @Test
    void login_failure_shouldReturnBadRequest() throws Exception {
        Mockito.when(userService.login("bad@example.com", "wrong"))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .param("email", "bad@example.com")
                        .param("password", "wrong"))
                .andExpect(status().isBadRequest());
    }
}
