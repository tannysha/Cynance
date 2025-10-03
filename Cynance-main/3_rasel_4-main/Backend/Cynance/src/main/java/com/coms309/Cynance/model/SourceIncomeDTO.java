package com.coms309.Cynance.model;

public class SourceIncomeDTO {
    private String source;
    private double total;

    public SourceIncomeDTO(String source, double total) {
        this.source = source;
        this.total = total;
    }

    public String getSource() {
        return source;
    }

    public double getTotal() {
        return total;
    }
}
