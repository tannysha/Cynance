package com.coms309.Cynance.model;

public class MaxSourceIncomeDTO {
    private String source;
    private double total;
    private IncomeSummary highestIncome;

    public MaxSourceIncomeDTO(String source, double total, IncomeSummary highestIncome) {
        this.source = source;
        this.total = total;
        this.highestIncome = highestIncome;
    }

    public String getSource() {
        return source;
    }

    public double getTotal() {
        return total;
    }

    public IncomeSummary getHighestIncome() {
        return highestIncome;
    }
}
