package com.coms309.Cynance.model;

public class ReactionDTO {
    private Long messageId;
    private Long userId;
    private String emoji;
    private boolean added;
    public ReactionDTO() {}

    public ReactionDTO(Long messageId, Long userId, String emoji, boolean added) {
        this.messageId = messageId;
        this.userId = userId;
        this.emoji = emoji;
        this.added = added;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }
}
