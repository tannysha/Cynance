package com.coms309.Cynance.model;

import jakarta.persistence.*;

@Entity
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emoji;

    @ManyToOne
    private User user;

    @ManyToOne
    private Message message;

    public Reaction() {}

    public Reaction(String emoji, User user, Message message) {
        this.emoji = emoji;
        this.user = user;
        this.message = message;
    }

    // Getters and setters
}

