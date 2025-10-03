package com.coms309.Cynance.repository;

import com.coms309.Cynance.model.Subscription;
import com.coms309.Cynance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    //Find all subscriptions for a user
    List<Subscription> findByUser(User user);

    // Find a subscription by title for a specific user
    Optional<Subscription> findByUserAndTitle(User user, String title);

    // Check if a subscription already exists for a user (alternative)
    boolean existsByUserAndTitle(User user, String title);

    @Query("SELECT s FROM Subscription s WHERE (LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND s.user.username = :username")
    List<Subscription> searchByKeyword(@Param("query") String query, @Param("username") String username);


    // boolean existsByUserAndSubscription(User user, Subscription subscription);
   // boolean existsByUserAndTitleAndSubscription(User user, String title, Subscription subscription);
   // boolean existsByUserAndSubscriptionAndTitle(User user, Subscription subscription, String title);
    //boolean existsByTitle(String title);
}
