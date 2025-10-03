package com.coms309.Cynance.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;  // Who receives the notification

    @Column(nullable = false)
    private String type;  // EXPENSE, INCOME, SUBSCRIPTION, etc.

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean seen = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Notification() {}

    public Notification(String username, String type, String message) {
        this.username = username;
        this.type = type;
        this.message = message;
        this.createdAt = new Date();
    }

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
