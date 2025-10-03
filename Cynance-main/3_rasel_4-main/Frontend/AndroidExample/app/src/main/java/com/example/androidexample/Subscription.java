package com.example.androidexample;

public class Subscription {
    private String title;
    private String startDate;
    private String endDate;
    private String price;

    public Subscription(String title, String startDate, String endDate, String price) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
    }

    // Getter methods
    public String getServiceName() { return title; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getPrice() { return price; }

    // Setter methods
    public void setServiceName(String title) { this.title = title; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setPrice(String price) { this.price = price; }
}
