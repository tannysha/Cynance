package com.coms309.Cynance.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BillPaymentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private boolean isPaid;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    private GroupBill bill;

    public BillPaymentStatus() {
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with convenience fields (optional)
    public BillPaymentStatus(User user, GroupBill bill, Double amount, boolean isPaid) {
        this.user = user;
        this.bill = bill;
        this.amount = amount;
        this.isPaid = isPaid;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    public GroupBill getBill() {
        return bill;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPaid(boolean paid) {
        this.isPaid = paid;
        this.updatedAt = LocalDateTime.now();
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBill(GroupBill bill) {
        this.bill = bill;
    }
}
