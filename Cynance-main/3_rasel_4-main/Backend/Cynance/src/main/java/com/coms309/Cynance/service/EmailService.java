package com.coms309.Cynance.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to Cynance 🎉");
        message.setText("Hi " + username + ",\n\nThanks for registering with Cynance!\nWe're glad to have you on board. 🚀");

        mailSender.send(message);
    }

    public void sendBanNotification(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Account Ban Notification ");
        message.setText("Hi " + username + ",\n\nYour account on Cynance has been banned.\nIf you believe this is a mistake, please contact support.");

        mailSender.send(message);
    }

}
