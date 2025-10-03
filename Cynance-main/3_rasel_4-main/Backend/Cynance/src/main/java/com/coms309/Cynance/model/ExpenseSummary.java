package com.coms309.Cynance.model;

public class ExpenseSummary {
    private String title;
    private String date;
    private String description;
    private double price;

    public ExpenseSummary(String title, String date, String description, double price) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}
