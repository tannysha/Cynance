package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupListActivity extends AppCompatActivity {

    private static final String TAG = "GroupListActivity";
    private static final String BACKEND_URL = "http://coms-3090-026.class.las.iastate.edu:8080";

    private ListView groupListView;
    private ArrayAdapter<String> groupAdapter;
    private ArrayList<String> groupNames = new ArrayList<>();
    private ArrayList<JSONObject> groups = new ArrayList<>();

    private String username;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        Log.d(TAG, "onCreate started");

        username = getIntent().getStringExtra("username");
        if (username == null || username.trim().isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            username = prefs.getString("USERNAME", null);
        }

        if (username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "Username not found. Please login again.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Username is null or empty. Exiting activity.");
            finish();
            return;
        }

        Log.d(TAG, "Username: " + username);

        groupListView = findViewById(R.id.groupListView);
        groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupNames);
        groupListView.setAdapter(groupAdapter);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Back button clicked");
            Intent intent = new Intent(GroupListActivity.this, CyShare.class);
            startActivity(intent);
            finish();
        });

        groupListView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                JSONObject selectedGroup = groups.get(position);
                String selectedGroupName = selectedGroup.getString("name");
                long selectedGroupId = selectedGroup.getLong("id");

                Log.d(TAG, "Selected group: " + selectedGroupName + " (ID: " + selectedGroupId + ")");

                SharedPreferences chatPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = chatPrefs.edit();
                editor.putString("username", username);
                editor.putLong("groupId", selectedGroupId);
                editor.apply();

                Intent intent = new Intent(GroupListActivity.this, GroupExpenseActivity.class);
                intent.putExtra("groupName", selectedGroupName);
                intent.putExtra("groupId", selectedGroupId);
                startActivity(intent);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing selected group JSON", e);
                Toast.makeText(this, "Error reading group data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: fetching groups...");
        fetchGroups();
    }

    private void fetchGroups() {
        String url = BACKEND_URL + "/group/user/" + username;
        Log.d(TAG, "Fetching groups from: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "Response received: " + response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        groupNames.clear();
                        groups.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject group = jsonArray.getJSONObject(i);
                            groups.add(group);
                            String groupName = group.getString("name");
                            groupNames.add(groupName);
                            Log.d(TAG, "Group added: " + groupName);
                        }

                        groupAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Loaded " + groupNames.size() + " groups.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error", e);
                        Toast.makeText(this, "Failed to parse groups", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "HTTP Status Code: " + error.networkResponse.statusCode);
                    }
                    Toast.makeText(this, "Error fetching groups. Check internet or server.", Toast.LENGTH_SHORT).show();
                });

        queue.add(getRequest);
    }
}
