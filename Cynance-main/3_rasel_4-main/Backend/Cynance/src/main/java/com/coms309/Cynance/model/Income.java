package com.coms309.Cynance.model;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "income")
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    private Double amount;
    private String source;
    private String frequency; // Example: "Monthly", "Weekly", "One-Time"

    @Column(columnDefinition = "TEXT")
    private String history;  // Stores list as CSV string

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;// Foreign Key Reference to User Table

    public Income() {}

    public Income(Double amount, String source, String frequency, List<Double> history, User user) {
        this.amount = amount;
        this.source = source;
        this.frequency = frequency;
        this.setHistory(history);
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<Double> getHistory() {
        return convertStringToList(history);
    }

    public void setHistory(List<Double> history) {
        this.history = convertListToString(history);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private String convertListToString(List<Double> history) {
        return history != null ? history.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) : "";
    }

    private List<Double> convertStringToList(String history) {
        return history != null && !history.isEmpty()
                ? Arrays.stream(history.split(","))
                .map(Double::parseDouble)
                .collect(Collectors.toList())
                : List.of();
    }
}
