package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.java_websocket.handshake.ServerHandshake;

import java.util.ArrayList;

public class GroupChat extends AppCompatActivity implements WebSocketListener {

    private EditText messageInput;
    private Button sendButton;
    private Button backButton;
    private ListView messageListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> messages = new ArrayList<>();

    private String username;
    private long groupId;
    private String groupName;

    private static final String BASE_WS_URL = "ws://coms-3090-026.class.las.iastate.edu:8080/chat/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);
        messageListView = findViewById(R.id.messageListView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        messageListView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = prefs.getString("username", null);
        groupId = prefs.getLong("groupId", -1);
        groupName = prefs.getString("groupName", null);

        if (username == null || groupId == -1 || groupName == null) {
            Toast.makeText(this, "Missing data in SharedPreferences", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Load previous chat messages
        loadPreviousMessages(groupId);

        // Connect WebSocket
        WebSocketManager.getInstance().setWebSocketListener(this);
        WebSocketManager.getInstance().connectWebSocket(BASE_WS_URL + username);

        sendButton.setOnClickListener(v -> {
            String content = messageInput.getText().toString().trim();
            if (!content.isEmpty()) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("content", content);
                    json.put("groupId", groupId);
                    WebSocketManager.getInstance().sendMessage(json.toString());
                    messageInput.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupChat.this, GroupExpenseActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("groupName", groupName);

            SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            appPrefs.edit().putString("USERNAME", username).apply();

            startActivity(intent);
            finish();
        });

        messageListView.setOnItemClickListener((parent, view, position, id) -> {
            String message = messages.get(position);
            String[] parts = message.split(":", 2); // split sender from content
            String sender = parts.length > 0 ? parts[0].trim() : "";

            if (!sender.equals(username)) {
                Toast.makeText(this, "You can only delete your own messages!", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Delete Message")
                    .setMessage("Are you sure you want to delete this message?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        messages.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        messageListView.setOnItemLongClickListener((parent, view, position, id) -> {
            messages.set(position, messages.get(position) + " 👍");
            adapter.notifyDataSetChanged();
            return true;
        });
    }

    private void loadPreviousMessages(long groupId) {
        String BASE_API_URL = "http://coms-3090-026.class.las.iastate.edu:8080/chat/" + groupId + "/messages";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BASE_API_URL, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject messageObj = response.getJSONObject(i);
                            String sender = messageObj.getString("senderUsername");
                            String content = messageObj.getString("content");
                            messages.add(sender + ": " + content);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            messages.add("[ERROR] Failed to load a message.");
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    error.printStackTrace();
                    messages.add("[ERROR] Could not load previous messages.");
                    adapter.notifyDataSetChanged();
                });

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
            try {
                if (message.trim().startsWith("{")) {
                    JSONObject json = new JSONObject(message);
                    String sender = json.getString("sender");
                    String content = json.getString("content");
                    messages.add(sender + ": " + content);
                } else {
                    messages.add(message);
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                messages.add("[ERROR] Failed to parse message: " + message);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        runOnUiThread(() -> {
            messages.add("Connection closed: " + reason);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        runOnUiThread(() -> {
            messages.add("Connected as " + username);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onWebSocketError(Exception ex) {
        runOnUiThread(() -> {
            messages.add("WebSocket error: " + ex.getMessage());
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onDestroy() {
        WebSocketManager.getInstance().disconnectWebSocket();
        WebSocketManager.getInstance().removeWebSocketListener();
        super.onDestroy();
    }
}
