package com.example.androidexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DeleteSubscriptionActivity extends AppCompatActivity {

    private EditText editTitle;
    private Button deleteButton;
    private String username;
    private static final String DELETE_SUB_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/subscriptions/remove";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_subscription);

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", "");

        // Initialize UI elements
        editTitle = findViewById(R.id.edit_title);
        deleteButton = findViewById(R.id.delete_button);

        // Delete button logic
        deleteButton.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(DeleteSubscriptionActivity.this, "Please enter a subscription title", Toast.LENGTH_SHORT).show();
            } else {
                deleteSubscription(title);
            }
        });
    }

    private void deleteSubscription(String title) {
        try {
            String encodedTitle = URLEncoder.encode(title, "UTF-8");
            String url = DELETE_SUB_URL + "/" + username + "/" + encodedTitle;

            StringRequest stringRequest = new StringRequest(
                    Request.Method.DELETE, url,
                    response -> {
                        Toast.makeText(DeleteSubscriptionActivity.this, "Subscription deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity
                    },
                    error -> {
                        String errorMsg = (error.networkResponse != null && error.networkResponse.data != null)
                                ? new String(error.networkResponse.data)
                                : "Unknown error";
                        Toast.makeText(DeleteSubscriptionActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
            );

            Volley.newRequestQueue(this).add(stringRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Encoding error", Toast.LENGTH_SHORT).show();
        }
    }
}
