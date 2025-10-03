package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.User;
import com.coms309.Cynance.service.EmailService;
import com.coms309.Cynance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
//@CrossOrigin(origins = "http://10.0.2.2:8080")

public class LoginAndSignupController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        boolean isAuth = userService.loginUser(user);
        Map<String, String> response = new HashMap<>();

        if (isAuth) {
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        String result = userService.registerUser(user);

        if (result.equals("Email already registered.") || result.equals("Username already taken.")) {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT); // 409 Conflict
        }
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        return new ResponseEntity<>("User registered successfully.", HttpStatus.CREATED); // 201 Created
    }
}
