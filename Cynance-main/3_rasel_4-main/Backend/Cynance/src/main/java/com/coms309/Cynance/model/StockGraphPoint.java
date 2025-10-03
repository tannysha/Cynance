package com.coms309.Cynance.model;

public class StockGraphPoint {
    private String date;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private double volume;

    public StockGraphPoint(String date, double openPrice, double highPrice, double lowPrice, double closePrice, double volume) {
        this.date = date;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
    }

    public String getDate() { return date; }
    public double getOpenPrice() { return openPrice; }
    public double getHighPrice() { return highPrice; }
    public double getLowPrice() { return lowPrice; }
    public double getClosePrice() { return closePrice; }
    public double getVolume() { return volume; }
}

