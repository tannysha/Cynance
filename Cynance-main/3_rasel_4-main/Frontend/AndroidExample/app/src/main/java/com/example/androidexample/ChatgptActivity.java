package com.example.androidexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatgptActivity extends AppCompatActivity {

    private EditText promptEditText;
    private TextView responseTextView;
    private String username;
    private static final String URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatgpt);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", "User");

        promptEditText = findViewById(R.id.promptEditText);
        responseTextView = findViewById(R.id.responseTextView);
        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(view -> sendChatRequest());
    }

    private void sendChatRequest() {
        String prompt = promptEditText.getText().toString();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("prompt", prompt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, requestBody,
                response -> {
                    try {
                        String gptResponse = response.getString("response");
                        responseTextView.setText(gptResponse);
                    } catch (JSONException e) {
                        responseTextView.setText("Response error");
                    }
                },
                error -> responseTextView.setText("Request failed: " + error.toString())
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
