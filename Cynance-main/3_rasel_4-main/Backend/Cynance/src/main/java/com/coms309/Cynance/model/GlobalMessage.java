package com.coms309.Cynance.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "global_messages")
public class GlobalMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;  // sender

    @Column(nullable = false)
    private String role;      // "admin" or "user"

    @Lob
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sent = new Date();

    public GlobalMessage() {}

    public GlobalMessage(String userName, String role, String content) {
        this.userName = userName;
        this.role = role;
        this.content = content;
        this.sent = new Date();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public Date getSent() {
        return sent;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSent(Date sent) {
        this.sent = sent;
    }
}
