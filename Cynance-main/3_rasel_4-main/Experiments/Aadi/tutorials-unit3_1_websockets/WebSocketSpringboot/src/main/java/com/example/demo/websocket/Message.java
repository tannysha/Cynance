package com.example.demo.websocket;

import java.util.Hashtable;
import java.util.Map;

public class Message {
    private String id;
    private String sender;
    private String content;
    private boolean isPrivate;
    private Map<String, String> reactions = new Hashtable<>();

    public Message(String id, String sender, String content, boolean isPrivate) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.isPrivate = isPrivate;
    }
    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public boolean isPrivate() { return isPrivate; }
    public Map<String, String> getReactions() { return reactions; }

    public void setId(String id) { this.id = id; }
    public void setSender(String sender) { this.sender = sender; }
    public void setContent(String content) { this.content = content; }
    public void setPrivate(boolean aPrivate) { isPrivate = aPrivate; }
    public void setReactions(Map<String, String> reactions) { this.reactions = reactions; }
}

