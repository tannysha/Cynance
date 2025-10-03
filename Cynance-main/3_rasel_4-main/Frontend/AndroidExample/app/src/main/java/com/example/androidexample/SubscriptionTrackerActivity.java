package com.example.androidexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.Subscription;
import com.example.androidexample.SubscriptionAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SubscriptionTrackerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubscriptionAdapter adapter;
    private List<Subscription> subscriptionList;
    private String url;
    private String username;
    private static final String SUB_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/subscriptions/add"; // your backend URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_tracker);

        // Retrieve SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", "");
        Log.d("DEBUG", "Username from SharedPreferences: " + username);

        Button updateSubscriptionButton = findViewById(R.id.update_subscription_button);
        updateSubscriptionButton.setOnClickListener(v -> {
            // Start a new Activity or show a dialog to update the subscription
            Intent intent = new Intent(SubscriptionTrackerActivity.this, UpdateSubscriptionActivity.class);
            startActivity(intent);
        });
        Button deleteSubscriptionButton = findViewById(R.id.delete_subscription_button);
        deleteSubscriptionButton.setOnClickListener(v -> {
            Intent intent = new Intent(SubscriptionTrackerActivity.this, DeleteSubscriptionActivity.class);
            startActivity(intent);
        });


        if (username == null || username.isEmpty()) {
            username = getIntent().getStringExtra("USERNAME");
            Log.d("DEBUG", "Username from Intent: " + username);
        }

        if (username == null || username.isEmpty()) {
            Log.e("SubscriptionTracker", "Username not provided");
            Toast.makeText(this, "Username not found, please log in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set up RecyclerView and Adapter
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subscriptionList = new ArrayList<>();

        // Updated to provide the click listener for editing subscriptions
        adapter = new SubscriptionAdapter(subscriptionList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subscription subscription = (Subscription) v.getTag();
                showEditSubscriptionDialog(subscription);
            }
        });

        recyclerView.setAdapter(adapter);

        // Set up FloatingActionButton for adding subscriptions
        FloatingActionButton fab = findViewById(R.id.fab_add_subscription);
        fab.setContentDescription("Add Subscription");
        fab.setOnClickListener(v -> showAddSubscriptionDialog());

        // Back button: pass the username back
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SubscriptionTrackerActivity.this, HomePageActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });

        // History button
        Button historyButton = findViewById(R.id.history_button);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(SubscriptionTrackerActivity.this, HistoryActivity.class);
            intent.putExtra("USERNAME", username);  // Pass the username to the new activity
            startActivity(intent);
        });
    }

    private void showAddSubscriptionDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_subscription, null);
        EditText serviceName = dialogView.findViewById(R.id.edit_service_name);
        EditText startDate = dialogView.findViewById(R.id.edit_start_date);
        EditText endDate = dialogView.findViewById(R.id.edit_end_date);
        EditText price = dialogView.findViewById(R.id.edit_price);
        Button pickStartDate = dialogView.findViewById(R.id.btn_pick_start_date);
        Button pickEndDate = dialogView.findViewById(R.id.btn_pick_end_date);

        pickStartDate.setOnClickListener(v -> showDatePickerDialog(startDate));
        pickEndDate.setOnClickListener(v -> showDatePickerDialog(endDate));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = serviceName.getText().toString().trim();
                    String start = startDate.getText().toString().trim();
                    String end = endDate.getText().toString().trim();
                    String priceValue = price.getText().toString().trim();

                    if (name.isEmpty() || start.isEmpty() || end.isEmpty() || priceValue.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        // Create a Subscription object
                        Subscription subscription = new Subscription(name, start, end, priceValue);
                        // Add the subscription to the list and notify the adapter
                        subscriptionList.add(subscription);
                        adapter.notifyDataSetChanged();
                        // Send the subscription to the backend
                        sendSubscriptionToBackend(subscription);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    editText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void sendSubscriptionToBackend(Subscription subscription) {
        // Construct URL with the valid username
        url = SUB_URL + "/" + username;  // e.g., http://.../api/subscriptions/add/john
        try {
            JSONObject subscriptionJson = new JSONObject();
            subscriptionJson.put("title", subscription.getServiceName());
            subscriptionJson.put("startDate", subscription.getStartDate());
            subscriptionJson.put("endDate", subscription.getEndDate());
            subscriptionJson.put("price", subscription.getPrice());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, subscriptionJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(SubscriptionTrackerActivity.this,
                                    "Subscription added successfully!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse != null) {
                                String statusCode = "Status Code: " + error.networkResponse.statusCode;
                                String response = new String(error.networkResponse.data);
                                Log.e("VolleyError", statusCode);
                                Log.e("VolleyError", response);
                            } else {
                                Log.e("VolleyError", "Error: " + error.getMessage());
                            }
                            Toast.makeText(SubscriptionTrackerActivity.this,
                                    "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    }
            );
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create JSON request", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditSubscriptionDialog(Subscription subscription) {
        // Here you can create and show a dialog for editing the subscription
        Toast.makeText(this, "Edit subscription: " + subscription.getServiceName(), Toast.LENGTH_SHORT).show();
    }
}
