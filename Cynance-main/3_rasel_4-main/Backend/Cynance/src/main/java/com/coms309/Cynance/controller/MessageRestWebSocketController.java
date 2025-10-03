package com.coms309.Cynance.controller;

import com.coms309.Cynance.config.ChatContextHolder;
import com.coms309.Cynance.model.ChatMessage;
import com.coms309.Cynance.model.Group;
import com.coms309.Cynance.model.Message;
import com.coms309.Cynance.model.User;
import com.coms309.Cynance.repository.GroupRepository;
import com.coms309.Cynance.repository.MessageRepository;
import com.coms309.Cynance.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
@ServerEndpoint("/chat/{username}")
public class MessageRestWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(MessageRestWebSocketController.class);

    private static final Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static final Map<String, Session> usernameSessionMap = new Hashtable<>();
    private static final Map<Session, Long> sessionGroupMap = new Hashtable<>();

    private static MessageRepository messageRepository;
    private static GroupRepository groupRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        logger.info("User connected: {}", username);
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);
        session.getBasicRemote().sendText("[SYSTEM] Connected to group chat as: " + username);
    }

    @OnMessage
    public void onMessage(Session session, String json) {
        try {
            String sender = sessionUsernameMap.get(session);
            ChatMessage msgPayload = mapper.readValue(json, ChatMessage.class);

            long groupId = msgPayload.getGroupId();
            sessionGroupMap.put(session, groupId);

            // Get repositories via context holder
            GroupRepository groupRepo = ChatContextHolder.getGroupRepository();
            MessageRepository messageRepo = ChatContextHolder.getMessageRepository();

            Group group = groupRepo.findByIdWithMembers(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found or has no members"));

            Message message = new Message(msgPayload.getContent(), sender, group);
            message.setTimestamp(LocalDateTime.now());
            messageRepo.save(message);

            // Send notifications to group members (except sender)
            NotificationService notificationService = ChatContextHolder.getNotificationService();

            for (User member : group.getMembers()) {
                if (!member.getUsername().equals(sender)) {
                    notificationService.notifyUser(
                            member.getUsername(),
                            "GROUP_CHAT",
                            "New message from " + sender + " in group '" + group.getName() + "'"
                    );
                }
            }


            ChatMessage broadcastMsg = ChatMessage.fromMessage(message);
            String broadcastJson = mapper.writeValueAsString(broadcastMsg);

            broadcastToGroup(groupId, broadcastJson);

        } catch (Exception e) {
            logger.error("Failed to process message", e);
        }
    }


    @OnClose
    public void onClose(Session session) {
        String username = sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
        sessionGroupMap.remove(session);
        logger.info("User disconnected: {}", username);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket error", throwable);
    }

    private void broadcastToGroup(Long groupId, String messageJson) {
        sessionGroupMap.forEach((session, sessionGroupId) -> {
            try {
                if (session.isOpen() && sessionGroupId.equals(groupId)) {
                    session.getBasicRemote().sendText(messageJson);
                }
            } catch (IOException e) {
                logger.error("Broadcast error", e);
            }
        });
    }
}
