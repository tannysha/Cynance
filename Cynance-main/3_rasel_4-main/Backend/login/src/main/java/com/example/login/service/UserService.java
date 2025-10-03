package com.example.login.service;



import com.example.login.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.login.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean loginUser(User user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());

        if (userOptional.isPresent()) {
            User storedUser = userOptional.get();
            return user.getPassword().equals(storedUser.getPassword()); // Directly return result
        }
        return false;
    }
}


