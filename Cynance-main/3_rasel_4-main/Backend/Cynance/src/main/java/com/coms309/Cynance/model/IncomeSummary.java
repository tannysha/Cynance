package com.coms309.Cynance.model;

public class IncomeSummary {
    private String source;
    private double amount;
    private String frequency;

    public IncomeSummary(String source, double amount, String frequency) {
        this.source = source;
        this.amount = amount;
        this.frequency = frequency;
    }

    public String getSource() {
        return source;
    }

    public double getAmount() {
        return amount;
    }

    public String getFrequency() {
        return frequency;
    }
}
