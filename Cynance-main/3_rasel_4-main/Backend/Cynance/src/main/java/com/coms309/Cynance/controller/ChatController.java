package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.ChatRequestDTO;
import com.coms309.Cynance.service.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatGPTService chatGPTService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequestDTO request) {
        String response = chatGPTService.generateResponse(request.getUsername(), request.getPrompt());
        return ResponseEntity.ok(Map.of("response", response));
    }
}