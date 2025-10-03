package com.example.androidexample;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    EditText searchQuery;
    Button searchBtn;
    ListView resultListView;
    ArrayAdapter<String> resultAdapter;
    List<String> results = new ArrayList<>();
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        username = getIntent().getStringExtra("USERNAME");

        searchQuery = findViewById(R.id.searchQuery);
        searchBtn = findViewById(R.id.searchBtn);
        resultListView = findViewById(R.id.searchResultsList);

        resultAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        resultListView.setAdapter(resultAdapter);

        searchBtn.setOnClickListener(v -> {
            String query = searchQuery.getText().toString().trim();
            if (!query.isEmpty()) {
                searchData(query);
            } else {
                Toast.makeText(this, "Please enter a search query.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchData(String query) {
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/api/search/" + username + "?query=" + Uri.encode(query);
        Log.d("SearchRequest", "URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API_RESPONSE", response.toString());
                        results.clear();

                        // ✅ Parse Expenses
                        if (response.has("expenses")) {
                            JSONArray expenses = response.getJSONArray("expenses");
                            for (int i = 0; i < expenses.length(); i++) {
                                JSONObject obj = expenses.getJSONObject(i);
                                Log.d("EXPENSE_OBJ", obj.toString());

                                String title = obj.optString("title", "No Title");
                                double amount = extractAmount(obj);
                                results.add("Expense: " + title + " - $" + amount);
                            }
                        }

                        // ✅ Parse Incomes
                        if (response.has("incomes")) {
                            JSONArray incomes = response.getJSONArray("incomes");
                            for (int i = 0; i < incomes.length(); i++) {
                                JSONObject obj = incomes.getJSONObject(i);
                                String source = obj.optString("source", "No Source");
                                double amount = obj.optDouble("amount", 0);
                                results.add("Income: " + source + " - $" + amount);
                            }
                        }

                        // ✅ Parse Subscriptions
                        if (response.has("subscriptions")) {
                            JSONArray subs = response.getJSONArray("subscriptions");
                            for (int i = 0; i < subs.length(); i++) {
                                JSONObject obj = subs.getJSONObject(i);
                                Log.d("SUBSCRIPTION_OBJ", obj.toString());

                                String title = obj.optString("title", "No Title");
                                double cost = extractAmount(obj);
                                results.add("Subscription: " + title + " - $" + cost);
                            }
                        }

                        if (results.isEmpty()) {
                            results.add("No matches found.");
                        }

                        resultAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    if (error.networkResponse != null) {
                        int code = error.networkResponse.statusCode;
                        Log.e("API_ERROR", "Status code: " + code);
                        Toast.makeText(this, "API error " + code, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Network/API error", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }

    // ✅ Helper to extract 'amount' or 'cost' or 'price' from an object
    private double extractAmount(JSONObject obj) {
        if (obj.has("amount")) return obj.optDouble("amount");
        if (obj.has("cost")) return obj.optDouble("cost");
        if (obj.has("price")) return obj.optDouble("price");
        return 0;
    }
}
