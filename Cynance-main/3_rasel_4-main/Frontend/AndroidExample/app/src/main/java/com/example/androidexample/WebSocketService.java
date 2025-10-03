package com.example.androidexample;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WebSocketService extends Service {

    private final Map<String, WebSocketClient> webSockets = new HashMap<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("CONNECT".equals(action)) {
                String url = intent.getStringExtra("url");
                String key = intent.getStringExtra("key");
                connectWebSocket(key, url);
            } else if ("DISCONNECT".equals(action)) {
                String key = intent.getStringExtra("key");
                disconnectWebSocket(key);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("SendWebSocketMessage"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (WebSocketClient client : webSockets.values()) {
            client.close();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void connectWebSocket(String key, String url) {
        try {
            URI serverUri = URI.create(url);
            Log.d("WebSocketService", "Connecting to: " + url);

            WebSocketClient webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(key, "WebSocket Connected");
                }

                @Override
                public void onMessage(String message) {
                    Intent intent = new Intent("WebSocketMessageReceived");
                    intent.putExtra("key", key);
                    intent.putExtra("message", message);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(key, "WebSocket Closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(key, "WebSocket Error", ex);
                }
            };

            webSocketClient.connect();
            webSockets.put(key, webSocketClient);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnectWebSocket(String key) {
        if (webSockets.containsKey(key)) {
            webSockets.get(key).close();
        }
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            String message = intent.getStringExtra("message");


            Log.d("WebSocketService", "onReceive() → key: " + key + ", message: " + message); // ✅ Debug log

            WebSocketClient webSocket = webSockets.get(key);
            if (webSocket != null && webSocket.isOpen()) {
                Log.d("WebSocketService", "Sending message: " + message + " via socket [" + key + "]"); // ✅ Sent confirmation
                webSocket.send(message);
            } else {
                Log.e("WebSocketService", "WebSocket not connected or closed for key: " + key);
            }
        }
    };
}
