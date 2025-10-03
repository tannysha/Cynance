package com.example.androidexample;

public class ChatMessage {
    private String content;
    private String senderRole;

    public ChatMessage(String content, String senderRole) {
        this.content = content;
        this.senderRole = senderRole;
    }

    public String getContent() {
        return content;
    }

    public String getSenderRole() {
        return senderRole;
    }
}
