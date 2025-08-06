package com.example.controller;

import com.example.dto.user.UserResponse;
import com.example.dto.user.UserUpdateRequest;
import com.example.model.Role;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserById_found_shouldReturnUser() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        Mockito.when(userService.getById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserById_notFound_shouldReturn404() throws Exception {
        Mockito.when(userService.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail_found_shouldReturnUser() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(2L);
        user.setEmail("email@example.com");

        Mockito.when(userService.getByEmail("email@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/email").param("value", "email@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.email").value("email@example.com"));
    }

    @Test
    void getUserByEmail_notFound_shouldReturn404() throws Exception {
        Mockito.when(userService.getByEmail("missing@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/email").param("value", "missing@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByUsername_found_shouldReturnUser() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(3L);
        user.setUsername("username");

        Mockito.when(userService.getByUsername("username")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/username").param("value", "username"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.username").value("username"));
    }

    @Test
    void getUserByUsername_notFound_shouldReturn404() throws Exception {
        Mockito.when(userService.getByUsername("no_user")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/username").param("value", "no_user"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByRole_shouldReturnList() throws Exception {
        UserResponse user1 = new UserResponse();
        user1.setId(1L);
        user1.setUsername("student1");
        UserResponse user2 = new UserResponse();
        user2.setId(2L);
        user2.setUsername("student2");

        Mockito.when(userService.getAllByRole(Role.STUDENT)).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users/role").param("value", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("student1"))
                .andExpect(jsonPath("$[1].username").value("student2"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("updatedUser");
        UserResponse updatedUser = new UserResponse();
        updatedUser.setId(1L);
        updatedUser.setUsername("updatedUser");

        Mockito.when(userService.update(eq(1L), any(UserUpdateRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("updatedUser"));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(1L);
    }

    @Test
    void deactivateUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/api/users/1/deactivate"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deactivate(1L);
    }

}
