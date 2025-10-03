package com.coms309.Cynance.model;

import java.time.LocalDateTime;

public class MessageDTO {
    private String senderUsername;
    private String content;
    private LocalDateTime timestamp;

    public MessageDTO(String senderUsername, String content, LocalDateTime timestamp) {
        this.senderUsername = senderUsername;
        this.content = content;
        this.timestamp = timestamp;
    }

    public MessageDTO(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

