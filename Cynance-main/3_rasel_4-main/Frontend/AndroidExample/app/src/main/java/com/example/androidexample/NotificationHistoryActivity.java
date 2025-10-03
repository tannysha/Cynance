package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NotificationHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private Button clearAllButton;
    private ArrayList<String> messages = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);

        listView = findViewById(R.id.notifications_list_view);
        clearAllButton = findViewById(R.id.clear_all_notifs_button);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationHistoryActivity.this, HomePageActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(adapter);

        username = getIntent().getStringExtra("USERNAME");
        if (username != null && !username.isEmpty()) {
            fetchUnseenNotifications(username);
        } else {
            messages.add("Username missing.");
            adapter.notifyDataSetChanged();
        }

        clearAllButton.setOnClickListener(v -> {
            if (username != null && !username.isEmpty()) {
                clearAllNotifications(username);
            }
        });
    }

    private void fetchUnseenNotifications(String username) {
        new Thread(() -> {
            try {
                URL url = new URL("http://coms-3090-026.class.las.iastate.edu:8080/notifications/" + username + "/unseen");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONArray array = new JSONArray(json.toString());
                messages.clear();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject notif = array.getJSONObject(i);
                    String msg = notif.getString("message");
                    String type = notif.getString("type");
                    String time = notif.getString("createdAt").replace("T", " ").split("\\.")[0];
                    messages.add("[" + type + "] " + msg + "\n" + time);
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    messages.clear();
                    messages.add("Failed to load unseen notifications.");
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private void clearAllNotifications(String username) {
        new Thread(() -> {
            try {
                URL url = new URL("http://coms-3090-026.class.las.iastate.edu:8080/notifications/" + username + "/clear");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

                int responseCode = conn.getResponseCode();

                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        messages.clear();
                        adapter.notifyDataSetChanged();
                    } else {
                        messages.clear();
                        messages.add("Failed to clear notifications.");
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    messages.clear();
                    messages.add("Error while clearing notifications.");
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }
}
