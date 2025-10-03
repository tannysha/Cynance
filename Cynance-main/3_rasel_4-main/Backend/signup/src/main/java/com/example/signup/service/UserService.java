package com.example.signup.service;

import com.example.signup.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.signup.model.User;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public String registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already registered.";
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "Username already taken.";
        }

        userRepository.save(user);
        return "User registered successfully.";
    }
}
