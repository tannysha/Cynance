package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.Message;
import com.coms309.Cynance.model.MessageDTO;
import com.coms309.Cynance.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<MessageDTO>> getChatMessages(@PathVariable Long groupId) {
        List<Message> messages = messageRepository.findByGroupIdOrderByTimestampAsc(groupId);

        List<MessageDTO> messageDTOs = messages.stream().map(message -> new MessageDTO(
                message.getSender(),
                message.getContent(),
                message.getTimestamp()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(messageDTOs);
    }
}
