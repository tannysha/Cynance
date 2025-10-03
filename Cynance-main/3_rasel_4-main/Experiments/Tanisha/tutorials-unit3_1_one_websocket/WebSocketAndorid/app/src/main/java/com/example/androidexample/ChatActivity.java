package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import org.java_websocket.handshake.ServerHandshake;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private Button sendBtn;
    private EditText msgEtx;
    private ListView msgListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        /* initialize UI elements */
        sendBtn = findViewById(R.id.sendBtn);
        msgEtx = findViewById(R.id.msgEdt);
        msgListView = findViewById(R.id.msgListView);

        /* initialize message list */
        messages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        msgListView.setAdapter(adapter);

        /* connect WebSocket */
        WebSocketManager.getInstance().setWebSocketListener(this);

        /* send button listener */
        sendBtn.setOnClickListener(v -> {
            try {
                String message = msgEtx.getText().toString();
                WebSocketManager.getInstance().sendMessage(message);
                msgEtx.setText("");
            } catch (Exception e) {
                Log.d("ExceptionSendMessage", e.getMessage());
            }
        });

        /* long press to react */
        msgListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String reactedMessage = messages.get(position) + " 👍"; // Add thumbs up emoji
            messages.set(position, reactedMessage);
            adapter.notifyDataSetChanged();
            return true;
        });

        /* swipe to delete (basic implementation) */
        msgListView.setOnItemClickListener((parent, view, position, id) -> {
            messages.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
            messages.add(message);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        runOnUiThread(() -> messages.add("Connection closed: " + reason));
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}
    @Override
    public void onWebSocketError(Exception ex) {}
}
