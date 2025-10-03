package com.example.androidexample;

import android.content.Intent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signupButton;
    private Button loginButton;
    private TextView messageText;
    private TextView successMessageText;

    private static final String SIGNUP_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/users/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.signup_username_edt);
        emailEditText = findViewById(R.id.signup_email_edt);
        passwordEditText = findViewById(R.id.signup_password_edt);
        signupButton = findViewById(R.id.signup_signup_btn);
        loginButton = findViewById(R.id.signup_login_btn);
        messageText = findViewById(R.id.signup_message_text);
        successMessageText = findViewById(R.id.signup_success_message);

        messageText.setVisibility(View.GONE);
        successMessageText.setVisibility(View.GONE);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    messageText.setText("Please fill all the fields.");
                    messageText.setVisibility(View.VISIBLE);
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    messageText.setText("Invalid email format.");
                    messageText.setVisibility(View.VISIBLE);
                    return;
                }

                registerUser(username, email, password);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser(final String username, final String email, final String password) {
        Log.d("SignupActivity", "Registering user...");

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            messageText.setText("Error creating request.");
            messageText.setVisibility(View.VISIBLE);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, SIGNUP_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("SignupResponse", "Response: " + response.toString());
                            String message = response.getString("message");

                            if (message.equals("User registered successfully.")) {
                                successMessageText.setVisibility(View.VISIBLE);
                                messageText.setVisibility(View.GONE);
                            } else {
                                messageText.setText(message);
                                messageText.setVisibility(View.VISIBLE);
                                successMessageText.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            messageText.setText("Invalid response from server.");
                            messageText.setVisibility(View.VISIBLE);
                            successMessageText.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 409) {
                                messageText.setText("Username or email already taken.");
                            } else {
                                messageText.setText("Error: " + statusCode);
                            }
                        } else {
                            messageText.setText("Network error: " + error.getMessage());
                        }
                        Log.e("SignupActivity", "Error: ", error);
                        successMessageText.setVisibility(View.GONE);
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
