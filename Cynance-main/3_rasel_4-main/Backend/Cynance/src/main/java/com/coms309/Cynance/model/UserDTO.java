package com.coms309.Cynance.model;

public class UserDTO {
    private Long id;
    private String username;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
}

