package com.coms309.Cynance.model;

import java.util.List;


public class BillRequest {

    private String paidByUsername;
    private double totalAmount;
    private String expenseType;
    private List<String> selectedUsernames;

    public BillRequest() {}

    public String getPaidByUsername() {
        return paidByUsername;
    }

    public void setPaidByUsername(String paidByUsername) {
        this.paidByUsername = paidByUsername;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public List<String> getSelectedUsernames() {
        return selectedUsernames;
    }

    public void setSelectedUsernames(List<String> selectedUsernames) {
        this.selectedUsernames = selectedUsernames;
    }
}
