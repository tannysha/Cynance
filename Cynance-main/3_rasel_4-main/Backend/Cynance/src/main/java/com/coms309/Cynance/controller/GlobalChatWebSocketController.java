package com.coms309.Cynance.controller;

import com.coms309.Cynance.config.util.GlobalChatContextHolder;
import com.coms309.Cynance.config.util.SpringContextHolder;
import com.coms309.Cynance.model.GlobalMessage;
import com.coms309.Cynance.model.User;
import com.coms309.Cynance.repository.GlobalMessageRepository;
import com.coms309.Cynance.repository.UserRepository;
import com.coms309.Cynance.service.NotificationService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;

@Controller
@ServerEndpoint(value = "/global-chat/{username}")
public class GlobalChatWebSocketController {

    private static final Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static final Map<String, Session> usernameSessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(GlobalChatWebSocketController.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        logger.info("User connected: " + username);

        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        sendMessageToUser(username, loadChatHistory());
        broadcast("[JOIN] " + username + " joined the global chat.");
    }

    @OnMessage
    public void onMessage(Session session, String messageContent) throws IOException {
        String sender = sessionUsernameMap.get(session);
        String role = sender.toLowerCase().startsWith("admin") ? "admin" : "user";

        String formattedMessage = "[" + role.toUpperCase() + "] " + sender + ": " + messageContent;
        broadcast(formattedMessage);

        // Admin message: notify ALL non-admin users (online or offline)
        if ("admin".equals(role)) {
            NotificationService notificationService = SpringContextHolder.getBean(NotificationService.class);
            UserRepository userRepository = SpringContextHolder.getBean(UserRepository.class);

            List<String> allNonAdminUsers = userRepository.findAll()
                    .stream()
                    .map(User::getUsername)
                    .filter(username -> !username.toLowerCase().startsWith("admin"))
                    .toList();

            for (String username : allNonAdminUsers) {
                notificationService.notifyUser(username, "CHAT", "Admin posted: " + messageContent);
            }
        }

        // Save message to DB
        GlobalMessageRepository msgRepo = GlobalChatContextHolder.getRepository();
        msgRepo.save(new GlobalMessage(sender, role, messageContent));
    }

    @OnClose
    public void onClose(Session session) {
        String username = sessionUsernameMap.get(session);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
        broadcast("[EXIT] " + username + " left the chat.");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket Error", throwable);
    }

    private void sendMessageToUser(String username, String message) {
        try {
            Session session = usernameSessionMap.get(username);
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            logger.error("Error sending message to " + username, e);
        }
    }

    private void broadcast(String message) {
        sessionUsernameMap.keySet().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                logger.error("Broadcast error", e);
            }
        });
    }

    private String loadChatHistory() {
        GlobalMessageRepository msgRepo = GlobalChatContextHolder.getRepository();
        List<GlobalMessage> messages = msgRepo.findTop50ByOrderBySentDesc();
        Collections.reverse(messages); // Show oldest first

        StringBuilder history = new StringBuilder("[Chat History]\n");
        for (GlobalMessage m : messages) {
            history.append("[")
                    .append(m.getSent()).append("] ")
                    .append("[").append(m.getRole().toUpperCase()).append("] ")
                    .append(m.getUserName()).append(": ")
                    .append(m.getContent()).append("\n");
        }
        return history.toString();
    }
}
