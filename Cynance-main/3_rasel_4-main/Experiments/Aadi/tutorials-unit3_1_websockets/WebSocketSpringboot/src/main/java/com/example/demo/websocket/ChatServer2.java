package com.example.demo.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;

import com.example.demo.websocket.Message;


/**
 * Represents a WebSocket chat server for handling real-time communication
 * between users. Each user connects to the server using their unique
 * username.
 *
 * This class is annotated with Spring's `@ServerEndpoint` and `@Component`
 * annotations, making it a WebSocket endpoint that can handle WebSocket
 * connections at the "/chat/{username}" endpoint.
 *
 * Example URL: ws://localhost:8080/chat/username
 *
 * The server provides functionality for broadcasting messages to all connected
 * users and sending messages to specific users.
 */
@ServerEndpoint("/chat/2/{username}")
@Component
public class ChatServer2 {

    // Store all socket session and their corresponding username
    // Two maps for the ease of retrieval by key
    private static Map < Session, String > sessionUsernameMap = new Hashtable < > ();
    private static Map < String, Session > usernameSessionMap = new Hashtable < > ();
    private static Map<String, Message> messageMap = new Hashtable<>();


    // server side logger
    private final Logger logger = LoggerFactory.getLogger(ChatServer2.class);

    /**
     * This method is called when a new WebSocket connection is established.
     *
     * @param session represents the WebSocket session for the connected user.
     * @param username username specified in path parameter.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {

        // server side log
        logger.info("[onOpen] " + username);

        // Handle the case of a duplicate username
        if (usernameSessionMap.containsKey(username)) {
            session.getBasicRemote().sendText("Username already exists");
            session.close();
        }
        else {
            // map current session with username
            sessionUsernameMap.put(session, username);

            // map current username with session
            usernameSessionMap.put(username, session);

            // send to the user joining in
            sendMessageToPArticularUser(username, "Welcome to the chat server, "+username);

            // send to everyone in the chat
            broadcast("User: " + username + " has Joined the Chat");
        }
    }

    /**
     * Handles incoming WebSocket messages from a client.
     *
     * @param session The WebSocket session representing the client's connection.
     * @param message The message received from the client.
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String username = sessionUsernameMap.get(session);
        logger.info("[onMessage] " + username + ": " + message);

        // Handle delete command
        if (message.startsWith("/delete")) {
            String[] parts = message.split("\\s+");
            if (parts.length == 2) {
                String messageIdToDelete = parts[1];
                Message msg = messageMap.get(messageIdToDelete);

                if (msg != null && msg.getSender().equals(username)) {
                    messageMap.remove(messageIdToDelete);

                    String deleteNotification = "[DELETE " + messageIdToDelete + "]";

                    if (msg.isPrivate()) {
                        sendMessageToPArticularUser(username, deleteNotification);
                        // Optional: store receiver & notify them too
                    } else {
                        broadcast(deleteNotification);
                    }
                } else {
                    sendMessageToPArticularUser(username, "Cannot delete message: not found or unauthorized.");
                }
            } else {
                sendMessageToPArticularUser(username, "Usage: /delete <messageId>");
            }
            return;
        }

        // Handle react command
        if (message.startsWith("/react")) {
            String[] parts = message.split("\\s+");
            if (parts.length == 3) {
                String messageId = parts[1];
                String emoji = parts[2];

                Message msg = messageMap.get(messageId);
                if (msg != null) {
                    msg.getReactions().put(username, emoji);

                    String reactionNotice = "[REACT] " + username + " reacted to " + messageId + " with " + emoji;
                    if (msg.isPrivate()) {
                        sendMessageToPArticularUser(msg.getSender(), reactionNotice);
                        if (!msg.getSender().equals(username)) {
                            sendMessageToPArticularUser(username, reactionNotice);
                        }
                        // Optional: store & notify receiver too if available
                    } else {
                        broadcast(reactionNotice);
                    }
                } else {
                    sendMessageToPArticularUser(username, "Message ID not found for reaction.");
                }
            } else {
                sendMessageToPArticularUser(username, "Usage: /react <messageId> <emoji>");
            }
            return;
        }

        // Handle Direct Messages
        if (message.startsWith("@")) {
            String[] split_msg =  message.split("\\s+");
            String destUserName = split_msg[0].substring(1); // @username
            String actualMessage = String.join(" ", Arrays.copyOfRange(split_msg, 1, split_msg.length));

            String messageId = UUID.randomUUID().toString();
            Message msg = new Message(messageId, username, actualMessage, true);
            messageMap.put(messageId, msg);

            sendMessageToPArticularUser(destUserName, "[DM " + messageId + " from " + username + "]: " + actualMessage);
            sendMessageToPArticularUser(username, "[DM " + messageId + " from " + username + "]: " + actualMessage);
        }

        // Handle Broadcast Messages
        else {
            String messageId = UUID.randomUUID().toString();
            Message msg = new Message(messageId, username, message, false);
            messageMap.put(messageId, msg);

            broadcast("[Public " + messageId + "] " + username + ": " + message);
        }
    }



    /**
     * Handles the closure of a WebSocket connection.
     *
     * @param session The WebSocket session that is being closed.
     */
    @OnClose
    public void onClose(Session session) throws IOException {

        // get the username from session-username mapping
        String username = sessionUsernameMap.get(session);

        // server side log
        logger.info("[onClose] " + username);

        // remove user from memory mappings
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

        // send the message to chat
        broadcast(username + " disconnected");
    }

    /**
     * Handles WebSocket errors that occur during the connection.
     *
     * @param session   The WebSocket session where the error occurred.
     * @param throwable The Throwable representing the error condition.
     */
    @OnError
    public void onError(Session session, Throwable throwable) {

        // get the username from session-username mapping
        String username = sessionUsernameMap.get(session);

        // do error handling here
        logger.info("[onError]" + username + ": " + throwable.getMessage());
    }

    /**
     * Sends a message to a specific user in the chat (DM).
     *
     * @param username The username of the recipient.
     * @param message  The message to be sent.
     */
    private void sendMessageToPArticularUser(String username, String message) {
        try {
            Session userSession = usernameSessionMap.get(username);
            if (userSession != null && userSession.isOpen()) {
                userSession.getBasicRemote().sendText(message);
            } else {
                logger.warn("User " + username + " not found or disconnected.");
            }
        } catch (IOException e) {
            logger.warn("[DM Exception] " + e.getMessage());
        }
    }


    /**
     * Broadcasts a message to all users in the chat.
     *
     * @param message The message to be broadcasted to all users.
     */
    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("[Broadcast Exception] " + e.getMessage());
            }
        });
    }
}