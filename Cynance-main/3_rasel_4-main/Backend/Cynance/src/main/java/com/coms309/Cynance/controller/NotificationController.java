package com.coms309.Cynance.controller;
import com.coms309.Cynance.model.Notification;
import com.coms309.Cynance.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{username}/unseen")
    public List<Notification> getUnseen(@PathVariable String username) {
        return notificationService.getUnseenNotifications(username);
    }

    @DeleteMapping("/{username}/clear")
    public ResponseEntity<String> clearAllNotifications(@PathVariable String username) {
        notificationService.deleteAllNotifications(username);
        return ResponseEntity.ok("All notifications deleted for " + username);
    }

}
