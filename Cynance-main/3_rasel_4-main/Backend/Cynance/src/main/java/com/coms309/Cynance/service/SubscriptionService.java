package com.coms309.Cynance.service;
import com.coms309.Cynance.model.Subscription;
import com.coms309.Cynance.model.User;
import com.coms309.Cynance.repository.SubscriptionRepository;
import com.coms309.Cynance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    //  Add a new subscription for a user
    public boolean addSubscription(String username, Subscription subscription) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return false;

        if (subscriptionRepository.findByUserAndTitle(user, subscription.getTitle()).isPresent()) {
            return false; // Subscription already exists
        }

        subscription.setUser(user);
        subscriptionRepository.save(subscription);
        return true;
    }

    // Remove a subscription
    public boolean removeSubscription(String username, String title) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return false;

        Optional<Subscription> subscription = subscriptionRepository.findByUserAndTitle(user, title);
        if (subscription.isEmpty()) return false;

        subscriptionRepository.delete(subscription.get());
        return true;
    }

    // Get all subscriptions for a user
    public List<Subscription> getSubscriptions(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return List.of(); // Return an empty list instead of null
        return subscriptionRepository.findByUser(user);
    }

    // Get a single subscription
    public Optional<Subscription> getSubscription(String username, String title) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Optional.empty();
        return subscriptionRepository.findByUserAndTitle(user, title);
    }

    //  Update subscription details
    public boolean updateSubscription(String username, String oldTitle, Subscription updatedSubscription) {
        // Find the user by username
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return false; // User does not exist

        // Find the existing subscription by user and title (using the old title)
        Optional<Subscription> existingSubscription = subscriptionRepository.findByUserAndTitle(user, updatedSubscription.getTitle());
        if (existingSubscription.isEmpty()) return false; // Subscription not found

        Subscription subscription = existingSubscription.get();

        // Update only necessary fields
        subscription.setPrice(updatedSubscription.getPrice());
        subscription.setStartDate(updatedSubscription.getStartDate());
        subscription.setEndDate(updatedSubscription.getEndDate());

        // Save updated subscription
        subscriptionRepository.save(subscription);
        return true;
    }

}
