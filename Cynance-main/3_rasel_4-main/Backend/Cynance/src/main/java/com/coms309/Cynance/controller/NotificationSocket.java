package com.coms309.Cynance.controller;

import com.coms309.Cynance.config.util.SpringContextHolder;
import com.coms309.Cynance.model.Notification;
import com.coms309.Cynance.service.NotificationService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Controller
@ServerEndpoint(value = "/notifications/{username}")
public class NotificationSocket {

    private static final Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static final Map<String, Session> usernameSessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(NotificationSocket.class);

    private final NotificationService notificationService;

    public NotificationSocket() {
        this.notificationService = SpringContextHolder.getBean(NotificationService.class);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        logger.info("WebSocket open for: " + username);

        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);
        notificationService.registerSession(username, session);

        // Send unseen notifications
        List<Notification> unseen = notificationService.getUnseenNotifications(username);
        unseen.forEach(n -> {
            try {
                session.getBasicRemote().sendText(n.getMessage());
            } catch (IOException e) {
                logger.error("Failed to send unseen notification to " + username, e);
            }
        });
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        logger.info("WebSocket closed for: " + username);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
        notificationService.unregisterSession(username);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        logger.info("NotificationSocket received message (ignored): " + message);
        // You can later use this to mark as seen or respond back
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("Notification WebSocket error:", throwable);
    }
}
