package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewUserInfoActivity extends AppCompatActivity {

    private TextView usernameText, emailText;
    private String username, email;
    private RequestQueue requestQueue;
    private static final String PROFILE_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_info);



        // Initialize views
        usernameText = findViewById(R.id.main_msg_txt);
        emailText = findViewById(R.id.main_email_txt_msg_txt);

        // Retrieve username and email from Intent or SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        username = getIntent().getStringExtra("USERNAME");

        if (username == null || username.isEmpty()) {
            username = sharedPreferences.getString("username", "defaultUsername");
        }

        Log.d("DEBUG", "Fetching data for username: " + username);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Fetch user info
        fetchUserInfo();
        // Back button
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewUserInfoActivity.this, ProfileActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });
    }


    private void fetchUserInfo() {
        String url = PROFILE_URL +"username/"+ username;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        username = response.getString("username");
                        email = response.getString("email");

                        // Display fetched data
                        usernameText.setText("Username: " + username);
                        emailText.setText("Email: " + email);
                    } catch (JSONException e) {
                        Log.e("ERROR", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(ViewUserInfoActivity.this, "Failed to parse user info", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ERROR", "Volley error: " + error.toString());
                    Toast.makeText(ViewUserInfoActivity.this, "Error fetching user info", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }
}
