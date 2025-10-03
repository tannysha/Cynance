package com.coms309.Cynance.model;

public class MaxCategoryBillDTO {
    private String category;
    private double total;
    private ExpenseSummary highestBill;

    public MaxCategoryBillDTO(String category, double total, ExpenseSummary highestBill) {
        this.category = category;
        this.total = total;
        this.highestBill = highestBill;
    }

    public String getCategory() {
        return category;
    }

    public double getTotal() {
        return total;
    }

    public ExpenseSummary getHighestBill() {
        return highestBill;
    }
}

