package com.coms309.Cynance;

import com.coms309.Cynance.controller.ChatController;
import com.coms309.Cynance.model.ChatRequestDTO;
import com.coms309.Cynance.service.ChatGPTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatGPTService chatGPTService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testChatEndpoint_ReturnsGPTResponse() throws Exception {
        // Arrange
        ChatRequestDTO request = new ChatRequestDTO();
        request.setUsername("testuser");
        request.setPrompt("Give me budget advice");

        String expectedResponse = "You should track your expenses and create a savings plan.";
        when(chatGPTService.generateResponse("testuser", "Give me budget advice")).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse));
    }
}
