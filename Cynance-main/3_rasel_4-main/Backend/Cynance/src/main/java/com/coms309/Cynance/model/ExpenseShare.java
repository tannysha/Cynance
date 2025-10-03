package com.coms309.Cynance.model;


import jakarta.persistence.*;
import java.util.List;

@Entity
public class ExpenseShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName;
    private String description;
    private double amount;
    private String payer;

    @ElementCollection
    private List<String> participants;

    public ExpenseShare() {}

    public ExpenseShare(String groupName, String description, double amount, String payer, List<String> participants) {
        this.groupName = groupName;
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.participants = participants;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPayer() { return payer; }
    public void setPayer(String payer) { this.payer = payer; }
    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }
}
