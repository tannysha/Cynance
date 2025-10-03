package com.example.androidexample;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView messageText;

    private static final String LOGIN_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/users/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        messageText = findViewById(R.id.login_message_text);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    messageText.setText("Please enter both username and password.");
                    return;
                }

                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("username", username);
                    requestBody.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                    messageText.setText("Error creating request.");
                    return;
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST, LOGIN_URL, requestBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("LoginResponse", "Full Response: " + response.toString());
                                    String message = response.getString("message");
                                    Log.d("LoginMessage", "Received message: '" + message + "'");
                                    messageText.setText(message);

                                    if (message.equalsIgnoreCase("Login successful")) {
                                        // Store the username in SharedPreferences
                                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                                        sharedPreferences.edit().putString("USERNAME", username).apply();

                                        try {


                                            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                                            intent.putExtra("USERNAME", username); // Optional: pass user data
                                            startActivity(intent);
                                            finish(); // Close LoginActivity so it doesn't stay in back stack

                                        } catch (Exception e) {
                                            Log.e("LoginActivity", "Failed to start HomePageActivity", e);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    messageText.setText("Invalid response from server.");
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.networkResponse != null) {
                                    int statusCode = error.networkResponse.statusCode;
                                    if (statusCode == 401) {
                                        messageText.setText("Invalid username or password.");
                                    } else {
                                        messageText.setText("Error: " + statusCode);
                                    }
                                } else {
                                    messageText.setText("Network error: " + error.getMessage());
                                }
                                Log.e("LoginActivity", "Error: ", error);
                            }
                        }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }
                };

                requestQueue.add(jsonObjectRequest);
            }
        });
    }
}
