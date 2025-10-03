package com.example.androidexample;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

public class AdminLoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button adminLoginBtn;
    private TextView messageText;

    private static final String ADMIN_LOGIN_URL = "http://coms-3090-026.class.las.iastate.edu:8080/admin/login";

    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        usernameInput = findViewById(R.id.admin_username);
        passwordInput = findViewById(R.id.admin_password);
        adminLoginBtn = findViewById(R.id.admin_login_btn);
        messageText = findViewById(R.id.admin_login_message_text);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        adminLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameInput.getText().toString().trim();
                password = passwordInput.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    messageText.setText("Please enter both username and password.");
                    return;
                }

                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        ADMIN_LOGIN_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("AdminLoginResponse", "Raw response: " + response);
                                messageText.setText(response);

                                if (response.trim().equalsIgnoreCase("Login successful.")) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                                    sharedPreferences.edit().putString("ADMIN_USERNAME", username).apply();

                                    Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
                                    intent.putExtra("USERNAME", username);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                                    messageText.setText("Invalid credentials.");
                                } else {
                                    messageText.setText("Login failed: " + error.toString());
                                }
                                Log.e("AdminLoginError", "Error: ", error);
                            }
                        }) {
                    @Override
                    public byte[] getBody() {
                        JSONObject requestBody = new JSONObject();
                        try {
                            requestBody.put("username", username);
                            requestBody.put("password", password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return requestBody.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }
                };

                requestQueue.add(stringRequest);
            }
        });
    }
}
//need comment for push
