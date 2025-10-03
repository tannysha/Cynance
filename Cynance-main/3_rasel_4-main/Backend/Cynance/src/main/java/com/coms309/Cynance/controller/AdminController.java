package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.Admin;
import com.coms309.Cynance.model.BugReport;
import com.coms309.Cynance.service.AdminService;
import com.coms309.Cynance.service.BugReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private BugReportService bugReportService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Admin admin) {
        boolean isValid = adminService.validateAdmin(admin.getUsername(), admin.getPassword());
        if (isValid) {
            return ResponseEntity.ok("Login successful.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
    }
    @PostMapping("/bug-report")
    public ResponseEntity<String> submitBug(@RequestBody BugReport bugReport) {
        bugReportService.submitBug(bugReport);
        return ResponseEntity.ok("Bug report submitted.");
    }

    // View all bug reports
    @GetMapping("/{adminUsername}/getBug")
    public ResponseEntity<List<BugReport>> getAllBugs() {
        return ResponseEntity.ok(bugReportService.getAllBugs());
    }

    @PutMapping("/{adminUsername}/ban/{targetUsername}")
    public ResponseEntity<String> banUser(
            @PathVariable String adminUsername,
            @PathVariable String targetUsername) {

        if (!adminService.isValidAdmin(adminUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Not a valid admin.");
        }

        boolean result = adminService.banUser(targetUsername);
        if (result) {
            return ResponseEntity.ok("User '" + targetUsername + "' has been banned.");
        } else {
            return ResponseEntity.badRequest().body("User not found.");
        }
    }

    @PutMapping("/{adminUsername}/unban/{targetUsername}")
    public ResponseEntity<String> unbanUser(
            @PathVariable String adminUsername,
            @PathVariable String targetUsername) {

        if (!adminService.isValidAdmin(adminUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Not a valid admin.");
        }

        boolean result = adminService.unbanUser(targetUsername);
        if (result) {
            return ResponseEntity.ok("User '" + targetUsername + "' has been unbanned.");
        } else {
            return ResponseEntity.badRequest().body("User not found.");
        }
    }



}
