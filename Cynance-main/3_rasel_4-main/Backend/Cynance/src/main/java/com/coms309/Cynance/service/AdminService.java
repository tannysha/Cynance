package com.coms309.Cynance.service;

import com.coms309.Cynance.model.Admin;
import com.coms309.Cynance.model.User;
import com.coms309.Cynance.repository.AdminRepository;
import com.coms309.Cynance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;


    public boolean validateAdmin(String username, String password) {
        Optional<Admin> admin = adminRepo.findByUsername(username);
        return admin.isPresent() && admin.get().getPassword().equals(password);
    }

    public boolean banUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBanned(true);
            userRepository.save(user);


            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                emailService.sendBanNotification(user.getEmail(), user.getUsername());
            }

            return true;
        }
        return false;
    }


    public boolean unbanUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBanned(false);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean isValidAdmin(String username) {
        return adminRepo.findByUsername(username).isPresent();
    }


}
