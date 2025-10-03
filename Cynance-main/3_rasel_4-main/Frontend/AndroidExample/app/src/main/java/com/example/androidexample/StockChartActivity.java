package com.example.androidexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StockChartActivity extends AppCompatActivity {

    private EditText etSymbol, etFrom, etTo;
    private Button btnFetch;
    private LineChart lineChart;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_chart);

        etSymbol = findViewById(R.id.etSymbol);
        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        btnFetch = findViewById(R.id.btnFetch);
        lineChart = findViewById(R.id.lineChart);

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchStockData();
            }
        });
    }

    private void fetchStockData() {
        String symbol = etSymbol.getText().toString().trim();
        String from = etFrom.getText().toString().trim();
        String to = etTo.getText().toString().trim();

        if (symbol.isEmpty() || from.isEmpty() || to.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://coms-3090-026.class.las.iastate.edu:8080/api/investments/graph?symbol=" + symbol + "&from=" + from + "&to=" + to;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(StockChartActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    StockChartResponse stockChartResponse = gson.fromJson(body, StockChartResponse.class);
                    runOnUiThread(() -> drawChart(stockChartResponse));
                } else {
                    runOnUiThread(() -> Toast.makeText(StockChartActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void drawChart(StockChartResponse data) {
        List<Entry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        for (int i = 0; i < data.graph.size(); i++) {
            StockGraphPoint point = data.graph.get(i);
            entries.add(new Entry(i, (float) point.closePrice));
            dates.add(point.date.substring(5));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Close Price");
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setText("Stock Prices");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate(); // refresh
    }

    // Models matching your Java backend classes
    public static class StockChartResponse {
        List<StockGraphPoint> graph;
        StockGraphPoint latest;
    }

    public static class StockGraphPoint {
        String date;
        @SerializedName("openPrice")
        double openPrice;
        @SerializedName("highPrice")
        double highPrice;
        @SerializedName("lowPrice")
        double lowPrice;
        @SerializedName("closePrice")
        double closePrice;
        @SerializedName("volume")
        double volume;
    }
}
