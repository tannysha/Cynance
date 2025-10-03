package com.example.androidexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ChatActivityAdmin extends AppCompatActivity {

    private Button sendBtn, backBtn;
    private EditText msgInput;
    private TextView chatView;
    private ScrollView chatScrollView;

    private static final String KEY = "admin_chat";
    private String adminUsername;
    private String lastDisplayedMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_admin);

        sendBtn = findViewById(R.id.sendBtn);
        backBtn = findViewById(R.id.backBtn);
        msgInput = findViewById(R.id.msgInput);
        chatView = findViewById(R.id.chatView);
        chatScrollView = findViewById(R.id.chatScroll);

        // Get the admin username
        adminUsername = getIntent().getStringExtra("USERNAME");
        if (adminUsername == null) adminUsername = "admin";

        // Start WebSocket connection
        String url = "ws://coms-3090-026.class.las.iastate.edu:8080/global-chat/" + adminUsername;
        Intent connectIntent = new Intent(this, WebSocketService.class);
        connectIntent.setAction("CONNECT");
        connectIntent.putExtra("key", KEY);
        connectIntent.putExtra("url", url);
        startService(connectIntent);

        sendBtn.setOnClickListener(v -> {
            String msg = msgInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                String fullMsg =  msg;

                Intent intent = new Intent("SendWebSocketMessage");
                intent.putExtra("key", KEY);
                intent.putExtra("message", fullMsg);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                msgInput.setText("");
            }
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY.equals(intent.getStringExtra("key"))) {
                String msg = intent.getStringExtra("message");

                // Avoid displaying the same message twice in a row
                if (msg.equals(lastDisplayedMessage)) return;

                chatView.append("\n" + msg);
                lastDisplayedMessage = msg;

                chatScrollView.post(() -> chatScrollView.fullScroll(ScrollView.FOCUS_DOWN));
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                messageReceiver, new IntentFilter("WebSocketMessageReceived"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }
}
