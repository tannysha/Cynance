package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CyShare extends AppCompatActivity {

    private EditText groupNameInput;
    private LinearLayout userInputContainer;
    private Button createGroupButton, backButton, viewGroupsButton, addUserFieldButton;

    private static final String BACKEND_URL = "http://coms-3090-026.class.las.iastate.edu:8080/group"; //tch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cyshare);

        groupNameInput = findViewById(R.id.groupNameInput);
        userInputContainer = findViewById(R.id.userInputContainer);
        createGroupButton = findViewById(R.id.createGroupButton);
        backButton = findViewById(R.id.back_button);
        viewGroupsButton = findViewById(R.id.viewGroupsButton);
        addUserFieldButton = findViewById(R.id.addUserFieldButton);

        addUserInputField();

        addUserFieldButton.setOnClickListener(v -> addUserInputField());

        createGroupButton.setOnClickListener(v -> createGroup());

        viewGroupsButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String username = prefs.getString("USERNAME", null);

            if (username == null) {
                Toast.makeText(this, "Username not found. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CyShare.this, GroupListActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CyShare.this, HomePageActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void addUserInputField() {
        EditText usernameInput = new EditText(this);
        usernameInput.setHint("Enter Username");
        usernameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        usernameInput.setTextColor(0xFFFFFFFF);
        usernameInput.setHintTextColor(0xFFFFFFFF);
        usernameInput.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
        userInputContainer.addView(usernameInput);
    }

    private void createGroup() {
        String groupName = groupNameInput.getText().toString().trim();

        if (groupName.isEmpty()) {
            Toast.makeText(this, "Please enter a group name.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String currentUser = prefs.getString("USERNAME", null);

        if (currentUser == null || currentUser.trim().isEmpty()) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray usernamesArray = new JSONArray();
        usernamesArray.put(currentUser); // Always add creator

        for (int i = 0; i < userInputContainer.getChildCount(); i++) {
            View child = userInputContainer.getChildAt(i);
            if (child instanceof EditText) {
                String username = ((EditText) child).getText().toString().trim();
                if (!username.isEmpty() && !username.equalsIgnoreCase(currentUser)) {
                    usernamesArray.put(username);
                }
            }
        }

        if (usernamesArray.length() == 0) {
            Toast.makeText(this, "Please enter at least one username.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", groupName);
            requestBody.put("usernames", usernamesArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating group JSON.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST,
                BACKEND_URL + "/createWithUsernames",
                requestBody,
                response -> {
                    Toast.makeText(this, "Group Created Successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CyShare.this, GroupListActivity.class);
                    intent.putExtra("username", currentUser);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error creating group. Check usernames or backend connection.", Toast.LENGTH_LONG).show();
                });

        queue.add(jsonRequest);
    }
}
