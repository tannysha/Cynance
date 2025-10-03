package com.coms309.Cynance.model;

public class ChatMessage {

    private Long messageId;
    private String sender;
    private String content;
    private Long groupId;
    private boolean edited = false;
    private boolean deleted = false;
    private String timestamp;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(String sender, String content, Long groupId) {
        this.sender = sender;
        this.content = content;
        this.groupId = groupId;
    }

    // Getters & Setters
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Factory method to convert Message entity to ChatMessage DTO
    public static ChatMessage fromMessage(Message msg) {
        ChatMessage cm = new ChatMessage(
                msg.getSender(),
                msg.getContent(),
                msg.getGroup().getId()
        );
        cm.setMessageId(msg.getId());
        cm.setEdited(msg.isEdited());
        cm.setDeleted(msg.isDeleted());
        cm.setTimestamp(msg.getTimestamp() != null ? msg.getTimestamp().toString() : null);
        return cm;
    }
}
