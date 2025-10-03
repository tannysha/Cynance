package com.coms309.Cynance.model;
import java.util.Map;

public class ChatRequestDTO {
    private String username;
    private String prompt;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
