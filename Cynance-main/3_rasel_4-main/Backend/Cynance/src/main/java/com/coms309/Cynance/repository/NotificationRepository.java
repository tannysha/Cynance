package com.coms309.Cynance.repository;

import com.coms309.Cynance.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUsername(String username);

    List<Notification> findByUsernameAndSeenFalse(String username);
}
