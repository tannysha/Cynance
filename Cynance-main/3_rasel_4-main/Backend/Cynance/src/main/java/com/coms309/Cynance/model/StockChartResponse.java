package com.coms309.Cynance.model;

import java.util.List;

public class StockChartResponse {
    private List<StockGraphPoint> graph;
    private StockGraphPoint latest;

    public StockChartResponse(List<StockGraphPoint> graph, StockGraphPoint latest) {
        this.graph = graph;
        this.latest = latest;
    }

    public List<StockGraphPoint> getGraph() {
        return graph;
    }

    public StockGraphPoint getLatest() {
        return latest;
    }
}

