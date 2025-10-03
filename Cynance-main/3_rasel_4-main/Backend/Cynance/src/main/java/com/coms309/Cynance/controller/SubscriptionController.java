package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.Subscription;
import com.coms309.Cynance.service.SubscriptionService;
import com.coms309.Cynance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions/")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @PostMapping("add/{username}")
    public ResponseEntity<Map<String, String>> addSubscription(@PathVariable("username") String username, @RequestBody Subscription subscription) {
        Map<String, String> response = new HashMap<>();

        if (!userService.existsByUsername(username)) {
            response.put("error", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (subscriptionService.existsByUserAndTitle(username, subscription.getTitle())) {
            response.put("error", "Subscription already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        boolean added = subscriptionService.addSubscription(username, subscription);
        if (added) {
            response.put("message", "Subscription added successfully.");
            return ResponseEntity.ok(response);
        }

        response.put("error", "Failed to add subscription.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @DeleteMapping("remove/{username}/{title}")
    public ResponseEntity<Map<String, String>> removeSubscription(
            @PathVariable("username") String username,
            @PathVariable("title") String title) {

        Map<String, String> response = new HashMap<>();

        boolean removed = subscriptionService.removeSubscription(username, title);
        if (removed) {
            response.put("message", "Subscription removed successfully");
            return ResponseEntity.ok(response);
        }

        response.put("error", "Subscription not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("list/{username}")
    public ResponseEntity<?> getSubscriptions(@PathVariable("username") String username) {
        List<Subscription> subscriptions = subscriptionService.getSubscriptions(username);
        return ResponseEntity.ok(subscriptions);  // Always return 200 OK
    }

    @GetMapping("get/{username}")
    public ResponseEntity<?> getSubscription(@PathVariable("username") String username, @RequestBody Map<String, String> requestBody) {
        String title = requestBody.get("title");
        Optional<Subscription> subscription = subscriptionService.getSubscription(username, title);

        if (subscription.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Subscription not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(subscription.get());
    }

    @PutMapping("update/{username}/{oldTitle}")
    public ResponseEntity<Map<String, String>> updateSubscription(
            @PathVariable("username") String username,
            @PathVariable("oldTitle") String oldTitle,
            @RequestBody Subscription updatedSub) {

        boolean updated = subscriptionService.updateSubscription(username, oldTitle, updatedSub);

        Map<String, String> response = new HashMap<>();
        if (updated) {
            response.put("message", "Subscription updated successfully");
            return ResponseEntity.ok(response);
        }

        response.put("error", "Subscription not found or already exists.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
