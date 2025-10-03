package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, newPasswordEditText, confirmPasswordEditText, currentPasswordEditText;
    private Button changeUsernameButton, changeEmailButton, changePasswordButton, deleteAccountButton;
    private String username;
    private RequestQueue requestQueue;

    private static final String PROFILE_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Retrieve the username from Intent or SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        username = getIntent().getStringExtra("USERNAME");

        if (username == null || username.isEmpty()) {
            username = sharedPreferences.getString("username", "");
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        Log.d("ProfileActivity", "Retrieved USERNAME: " + username);

        // Initialize "View User Info" button
        Button viewUserInfoButton = findViewById(R.id.view_user_info_btn);

        // Set click listener
        viewUserInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ViewUserInfoActivity.class);
            intent.putExtra("USERNAME", username); // Pass username if needed
            startActivity(intent);
        });
        // Initialize UI elements
        usernameEditText = findViewById(R.id.profile_username_edt);
        emailEditText = findViewById(R.id.profile_email_edt);
        newPasswordEditText = findViewById(R.id.new_password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        currentPasswordEditText = findViewById(R.id.current_password);

        changeUsernameButton = findViewById(R.id.change_username_btn);
        changeEmailButton = findViewById(R.id.change_email_btn);
        changePasswordButton = findViewById(R.id.change_password_btn);
        deleteAccountButton = findViewById(R.id.delete_account_btn);

        requestQueue = Volley.newRequestQueue(this);

        // Button click listeners
        changeUsernameButton.setOnClickListener(v -> updateUsername());
        changeEmailButton.setOnClickListener(v -> changeEmail());
        changePasswordButton.setOnClickListener(v -> changePassword());
        deleteAccountButton.setOnClickListener(v -> confirmDeleteAccount());

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            // Redirect to login screen
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Back button
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });
    }


    private void changeEmail() {
        String newEmail = emailEditText.getText().toString().trim();

        if (newEmail.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = PROFILE_URL + "update/email/" + username;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("newEmail", newEmail);
        } catch (JSONException e) {
            Log.e("ProfileActivity", "JSON Error in changeEmail()", e);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    Toast.makeText(ProfileActivity.this, "Email updated successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("ProfileActivity", "Email Update Error: ", error);
                    Toast.makeText(ProfileActivity.this, "Email update failed.", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    /**
     * Change user password
     */
    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill out all password fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = PROFILE_URL + "update/password/" + username;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("oldPassword", currentPassword);
            jsonBody.put("newPassword", newPassword);
        } catch (JSONException e) {
            Log.e("ProfileActivity", "JSON Error in changePassword()", e);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    Toast.makeText(ProfileActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("ProfileActivity", "Password Change Error: ", error);
                    Toast.makeText(ProfileActivity.this, "Password change failed.", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }


    /**
     * Show a confirmation dialog before deleting the account
     */
    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteAccountDirectly()) // Calls the delete method after confirmation
                .setNegativeButton("No", null) // Dismisses the dialog if the user selects "No"
                .show();
    }

    /**
     * Delete user account
     */
    private void deleteAccountDirectly() {
        String url = PROFILE_URL + "delete/" + username;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(ProfileActivity.this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();

                    // Clear stored user session data
                    SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();

                    // Redirect to login screen
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Log.e("ProfileActivity", "Account Deletion Error: ", error);
                    Toast.makeText(ProfileActivity.this, "Failed to delete account.", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }



    /**
     * Fetch user profile data
     */
    private void fetchUserProfile() {
        String url = PROFILE_URL + username;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        usernameEditText.setText(response.getString("username"));
                        emailEditText.setText(response.getString("email"));
                    } catch (JSONException e) {
                        Toast.makeText(ProfileActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ProfileActivity.this, "Error loading profile.", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    /**
     * Update username correctly
     */
    private void updateUsername() {
        String newUsername = usernameEditText.getText().toString();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = PROFILE_URL + "update/username/" + username;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("newUsername", newUsername);
        } catch (JSONException e) {
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    Toast.makeText(ProfileActivity.this, "Username updated successfully!", Toast.LENGTH_SHORT).show();

                    // Update local variable
                    username = newUsername;

                    // Store in SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
                    editor.putString("username", username);
                    editor.apply();

                    // Return updated username to HomePageActivity
                    Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(ProfileActivity.this, "Username update failed.", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }
}
