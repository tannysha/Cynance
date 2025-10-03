package com.coms309.Cynance;

import com.coms309.Cynance.controller.AlterUserInfoController;
import com.coms309.Cynance.model.User;
import com.coms309.Cynance.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlterUserInfoController.class)
public class AlterUserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetUserInfoSuccess() throws Exception {
        User mockUser = new User(1L, "testuser", "pass", "test@example.com");
        Mockito.when(userService.getUserByUsername("testuser")).thenReturn(mockUser);

        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetUserInfoNotFound() throws Exception {
        Mockito.when(userService.getUserByUsername("missinguser")).thenReturn(null);

        mockMvc.perform(get("/api/users/username/missinguser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found."));
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        Mockito.when(userService.deleteUser("testuser")).thenReturn(true);

        mockMvc.perform(delete("/api/users/delete/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete successful"));
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        Mockito.when(userService.deleteUser("missinguser")).thenReturn(false);

        mockMvc.perform(delete("/api/users/delete/missinguser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found."));
    }

    @Test
    void testUpdatePasswordSameAsOld() throws Exception {
        Map<String, String> body = Map.of("oldPassword", "abc123", "newPassword", "abc123");

        mockMvc.perform(put("/api/users/update/password/testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("New password cannot be the same as the old password."));
    }

    @Test
    void testUpdatePasswordSuccess() throws Exception {
        Map<String, String> body = Map.of("oldPassword", "abc123", "newPassword", "newpass");
        Mockito.when(userService.updatePassword("testuser", "newpass", "abc123")).thenReturn(true);

        mockMvc.perform(put("/api/users/update/password/testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"));
    }

    @Test
    void testUpdateUsernameSuccess() throws Exception {
        Map<String, String> body = Map.of("newUsername", "newuser");
        Mockito.when(userService.updateUsername("olduser", "newuser")).thenReturn(true);

        mockMvc.perform(put("/api/users/update/username/olduser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Username updated successfully"));
    }

    @Test
    void testUpdateEmailSuccess() throws Exception {
        Map<String, String> body = Map.of("newEmail", "new@example.com");
        Mockito.when(userService.updateEmail("testuser", "new@example.com")).thenReturn(true);

        mockMvc.perform(put("/api/users/update/email/testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email updated successfully"));
    }
}

