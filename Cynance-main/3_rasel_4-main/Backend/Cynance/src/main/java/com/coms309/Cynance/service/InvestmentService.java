package com.coms309.Cynance.service;

import com.coms309.Cynance.model.StockChartResponse;
import com.coms309.Cynance.model.StockGraphPoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class InvestmentService {

    private final String API_KEY = "3aa5af3d59e34bf08a2b9eff51646385";


    public StockChartResponse getStockChart(String symbol, String fromDateStr, String toDateStr) {
        try {
            String url = String.format(
                    "https://api.twelvedata.com/time_series?symbol=%s&interval=1day&start_date=%s&end_date=%s&apikey=%s",
                    symbol, fromDateStr, toDateStr, API_KEY
            );

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JSONObject json = new JSONObject(sb.toString());
            JSONArray values = json.getJSONArray("values");

            List<StockGraphPoint> graph = new ArrayList<>();
            for (int i = values.length() - 1; i >= 0; i--) { // Chronological order
                JSONObject obj = values.getJSONObject(i);
                graph.add(new StockGraphPoint(
                        obj.getString("datetime"),
                        Double.parseDouble(obj.getString("open")),
                        Double.parseDouble(obj.getString("high")),
                        Double.parseDouble(obj.getString("low")),
                        Double.parseDouble(obj.getString("close")),
                        Double.parseDouble(obj.getString("volume"))
                ));
            }

            StockGraphPoint latest = graph.get(graph.size() - 1);
            return new StockChartResponse(graph, latest);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching stock data: " + e.getMessage());
        }
    }
}
