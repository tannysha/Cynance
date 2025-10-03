package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminActivity extends AppCompatActivity {

    private Button banUserBtn, faqBtn, reportBugBtn;
    private EditText banUsernameInput, bugReportInput;

    private static final String BASE_BAN_URL = "http://coms-3090-026.class.las.iastate.edu:8080/admin";
    private static final String BUG_REPORT_URL = "http://coms-3090-026.class.las.iastate.edu:8080/admin/bug-report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        banUserBtn = findViewById(R.id.ban_user_btn);
        faqBtn = findViewById(R.id.faq_btn);
        reportBugBtn = findViewById(R.id.report_bug_btn);
        banUsernameInput = findViewById(R.id.ban_username_input);
        bugReportInput = findViewById(R.id.bug_report_input);



        RequestQueue requestQueue = Volley.newRequestQueue(this);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String adminUsername = sharedPreferences.getString("ADMIN_USERNAME", null);

        faqBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ChatActivityAdmin.class);
            startActivity(intent);
        });


        // Ban user logic
        banUserBtn.setOnClickListener(v -> {
            String targetUsername = banUsernameInput.getText().toString().trim();

            if (adminUsername == null) {
                Toast.makeText(this, "Admin username not found. Please re-login.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (targetUsername.isEmpty()) {
                Toast.makeText(this, "Please enter a username to ban.", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullUrl = BASE_BAN_URL + "/" + adminUsername + "/ban/" + targetUsername;

            StringRequest deleteRequest = new StringRequest(
                    Request.Method.PUT,
                    fullUrl,
                    response -> Toast.makeText(this, "Success: " + response, Toast.LENGTH_LONG).show(),
                    error -> {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 403) {
                            Toast.makeText(this, "Access denied: Not a valid admin.", Toast.LENGTH_SHORT).show();
                        } else if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error banning user.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            requestQueue.add(deleteRequest);
        });

        // FAQ click


        // Report bug logic
        reportBugBtn.setOnClickListener(v -> {
            String bugDescription = bugReportInput.getText().toString().trim();

            if (bugDescription.isEmpty()) {
                Toast.makeText(this, "Please describe the bug before submitting.", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("description", bugDescription);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating bug report.", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest bugRequest = new StringRequest(
                    Request.Method.POST,
                    BUG_REPORT_URL,
                    response -> Toast.makeText(this, "Bug submitted successfully!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(this, "Failed to submit bug report.", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                public byte[] getBody() {
                    return requestBody.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            requestQueue.add(bugRequest);
        });
        faqBtn.setOnClickListener(v -> {
            String url = "ws://coms-3090-026.class.las.iastate.edu:8080/global-chat/" + adminUsername;

            // Start WebSocketService for admin chat
            Intent serviceIntent = new Intent(AdminActivity.this, WebSocketService.class);
            serviceIntent.setAction("CONNECT");
            serviceIntent.putExtra("key", "admin_chat");
            serviceIntent.putExtra("url", url);
            startService(serviceIntent);

            // Open chat activity
            Intent chatIntent = new Intent(AdminActivity.this, ChatActivityAdmin.class);
            startActivity(chatIntent);
        });





    }
}
