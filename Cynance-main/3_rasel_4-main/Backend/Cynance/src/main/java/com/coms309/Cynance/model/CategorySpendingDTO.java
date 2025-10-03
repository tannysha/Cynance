package com.coms309.Cynance.model;

public class CategorySpendingDTO {
    private String category;
    private double total;

    public CategorySpendingDTO(String category, double total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public double getTotal() {
        return total;
    }
}