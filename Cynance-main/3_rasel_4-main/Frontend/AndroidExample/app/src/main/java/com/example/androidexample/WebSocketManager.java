package com.example.androidexample;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketManager {

    private static WebSocketManager instance;
    private WebSocketClient webSocketClient;
    private WebSocketListener listener;

    private WebSocketManager() {}

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void setWebSocketListener(WebSocketListener listener) {
        this.listener = listener;
    }

    public void removeWebSocketListener() {
        this.listener = null;
    }

    public void connectWebSocket(String serverUrl) {
        try {
            URI uri = new URI(serverUrl);
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("WebSocket", "Connected");
                    if (listener != null) {
                        listener.onWebSocketOpen(handshakedata);
                    }
                }

                @Override
                public void onMessage(String message) {
                    Log.d("WebSocket", "Message: " + message);
                    if (listener != null) {
                        listener.onWebSocketMessage(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("WebSocket", "Closed: " + reason);
                    if (listener != null) {
                        listener.onWebSocketClose(code, reason, remote);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.e("WebSocket", "Error", ex);
                    if (listener != null) {
                        listener.onWebSocketError(ex);
                    }
                }
            };
            webSocketClient.connect();
        } catch (Exception e) {
            Log.e("WebSocket", "Connection Failed", e);
        }
    }

    public void sendMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        }
    }

    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
