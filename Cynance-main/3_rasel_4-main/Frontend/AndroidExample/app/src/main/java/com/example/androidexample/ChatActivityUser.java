package com.example.androidexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatActivityUser extends AppCompatActivity {

    private static final String KEY = "user_chat";
    private Button sendBtn, backBtn;
    private EditText msgInput;
    private RecyclerView chatRecyclerView;
    private ChatMessageAdapter messageAdapter;
    private ArrayList<ChatMessage> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);

        sendBtn = findViewById(R.id.sendBtn);
        backBtn = findViewById(R.id.backBtn);
        msgInput = findViewById(R.id.msgInput);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        chatMessages = new ArrayList<>();
        messageAdapter = new ChatMessageAdapter(chatMessages);
        chatRecyclerView.setAdapter(messageAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sendBtn.setOnClickListener(v -> {
            String msg = msgInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                Intent intent = new Intent("SendWebSocketMessage");
                intent.putExtra("key", KEY);
                intent.putExtra("message", msg);
                Log.d("ChatActivityUser", "Sending: " + msg);
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
                String role = msg.contains("[ADMIN]") ? "admin" : "user";
                chatMessages.add(new ChatMessage(msg, role));
                messageAdapter.notifyItemInserted(chatMessages.size() - 1);
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
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