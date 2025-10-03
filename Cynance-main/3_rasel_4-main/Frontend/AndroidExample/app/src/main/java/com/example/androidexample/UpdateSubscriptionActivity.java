package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class UpdateSubscriptionActivity extends AppCompatActivity {

    private EditText editOldTitle, editNewTitle, editStartDate, editEndDate, editPrice;
    private Button saveButton;
    private String username;
    private static final String UPDATE_SUB_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/subscriptions/update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_subscription);

        // Retrieves username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", "");

        // Initializing UI elements
        editOldTitle = findViewById(R.id.edit_old_title);
        editNewTitle = findViewById(R.id.edit_new_title);
        editStartDate = findViewById(R.id.edit_start_date);
        editEndDate = findViewById(R.id.edit_end_date);
        editPrice = findViewById(R.id.edit_price);
        saveButton = findViewById(R.id.save_button);

        // Save button logic
        saveButton.setOnClickListener(v -> {
            String oldTitle = editOldTitle.getText().toString().trim();
            String newTitle = editNewTitle.getText().toString().trim();
            String startDate = editStartDate.getText().toString().trim();
            String endDate = editEndDate.getText().toString().trim();
            String price = editPrice.getText().toString().trim();

            if (oldTitle.isEmpty() || newTitle.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || price.isEmpty()) {
                Toast.makeText(UpdateSubscriptionActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                updateSubscription(oldTitle, newTitle, startDate, endDate, price);
            }
        });
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateSubscriptionActivity.this, SubscriptionTrackerActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });
    }

    private void updateSubscription(String oldTitle, String newTitle, String startDate, String endDate, String price) {
        try {
            JSONObject subscriptionJson = new JSONObject();
            subscriptionJson.put("title", newTitle);
            subscriptionJson.put("startDate", startDate);
            subscriptionJson.put("endDate", endDate);
            subscriptionJson.put("price", price);

            String url = UPDATE_SUB_URL + "/" + username + "/" + oldTitle;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, url, subscriptionJson,
                    response -> {
                        Toast.makeText(UpdateSubscriptionActivity.this, "Subscription updated successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to previous activity
                    },
                    error -> Toast.makeText(UpdateSubscriptionActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(UpdateSubscriptionActivity.this, "Error updating subscription", Toast.LENGTH_SHORT).show();
        }
    }
}

