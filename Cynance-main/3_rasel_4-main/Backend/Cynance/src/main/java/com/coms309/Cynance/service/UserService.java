package com.coms309.Cynance.service;

import com.coms309.Cynance.model.User;
import org.springframework.stereotype.Service;
import com.coms309.Cynance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

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


    public boolean loginUser(User user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());

        if (userOptional.isPresent()) {
            User storedUser = userOptional.get();

            if (storedUser.isBanned()) {
                return false;
            }
            return user.getPassword().equals(storedUser.getPassword());
        }

        return false;
    }


    public boolean deleteUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return true;
        }
        return false;

    }
    public boolean updatePassword(String username,String newPassword,String oldPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User storedUser = userOptional.get();
            if(storedUser.getPassword().equals(oldPassword)) {
                storedUser.setPassword(newPassword);
                userRepository.save(storedUser);
                return true;
            }
            return false;

        }
        return false;

    }

    public boolean updateUsername(String username,String newUsername) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            userOptional.get().setUsername(newUsername);
            userRepository.save(userOptional.get());
            return true;
        }
        return false;
    }

    public boolean updateEmail(String username,String newEmail) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            userOptional.get().setEmail(newEmail);
            userRepository.save(userOptional.get());
            return true;
        }
        return false;
    }

}
