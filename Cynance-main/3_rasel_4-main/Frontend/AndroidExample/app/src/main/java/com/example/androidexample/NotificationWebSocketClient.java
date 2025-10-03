package com.example.androidexample;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class NotificationWebSocketClient extends WebSocketClient {

    private static final String TAG = "NotificationWS";
    private static final String CHANNEL_ID = "notif_channel";
    private final Context context;

    public NotificationWebSocketClient(String username, Context context) throws Exception {
        super(new URI("ws://coms-3090-026.class.las.iastate.edu:8080/notifications/" + username));
        this.context = context;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Log.d(TAG, "WebSocket Opened");
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "Notification Received: " + message);
        showNotification("New Alert", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "WebSocket Closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "WebSocket Error", ex);
    }

    @SuppressLint("MissingPermission")
    private void showNotification(String title, String message) {
        createChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Notification Channel", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Shows real-time notifications");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
