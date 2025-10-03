package com.example.signup.controller;

import com.example.signup.model.User;
import com.example.signup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        String result = userService.registerUser(user);

        if (result.equals("Email already registered.") || result.equals("Username already taken.")) {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT); // 409 Conflict
        }

        return new ResponseEntity<>("User registered successfully.", HttpStatus.CREATED); // 201 Created
    }

}
