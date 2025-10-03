package com.coms309.Cynance.service;

import com.coms309.Cynance.model.Notification;
import com.coms309.Cynance.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepo;

    // Store live WebSocket sessions
    private final Map<String, Session> userSessions = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("NotificationService initialized and ready.");
    }

    // Called by WebSocket to register session
    public void registerSession(String username, Session session) {
        userSessions.put(username, session);
    }

    // Called by WebSocket to remove session
    public void unregisterSession(String username) {
        userSessions.remove(username);
    }

    // Save and send a notification
    public void notifyUser(String username, String type, String message) {
        Notification notif = new Notification(username, type, message);
        notificationRepo.save(notif);

        sendToUser(username, message);
    }

    // Just send via WebSocket (no DB)
    public void sendToUser(String username, String message) {
        Session session = userSessions.get(username);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                System.err.println("Failed to send notification to " + username + ": " + e.getMessage());
            }
        }
    }

    public List<Notification> getUnseenNotifications(String username) {
        return notificationRepo.findByUsernameAndSeenFalse(username);
    }

    public void markAllAsSeen(String username) {
        List<Notification> unseen = getUnseenNotifications(username);
        unseen.forEach(n -> n.setSeen(true));
        notificationRepo.saveAll(unseen);
    }
    public void deleteAllNotifications(String username) {
        List<Notification> userNotifs = notificationRepo.findByUsername(username);
        notificationRepo.deleteAll(userNotifs);
    }

}
