package com.coms309.Cynance;

import jakarta.websocket.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class NotificationClientEndpoint {

    private static CountDownLatch testLatch;
    private static List<String> testMessages;

    // Required no-arg constructor
    public NotificationClientEndpoint() {}

    public static void setTestContext(CountDownLatch latch, List<String> messages) {
        testLatch = latch;
        testMessages = messages;
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        if (testMessages != null) testMessages.add(message);
        if (testLatch != null) testLatch.countDown();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket closed: " + reason.getReasonPhrase());
    }
}
