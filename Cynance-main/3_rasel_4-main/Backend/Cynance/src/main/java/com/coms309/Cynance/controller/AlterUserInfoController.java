package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.User;
import com.coms309.Cynance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users/")
public class AlterUserInfoController {
    @Autowired
    private UserService userService;
//put functionality of not using the same username, password email as the same one when updating

    @GetMapping("username/{username}")
    public ResponseEntity<Map<String, String>> getUserInfo(@PathVariable("username") String username) {
        User user = userService.getUserByUsername(username);
        Map<String, String> response = new HashMap<>();

        if (user != null) {
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @DeleteMapping("delete/{username}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("username") String username) {//using the same method signature everywhere so as to get the ability to always return json objects
        boolean isDeleted = userService.deleteUser(username);
        Map<String, String> response = new HashMap<>();

        if (isDeleted) {
            response.put("message", "Delete successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("update/password/{username}")
    public ResponseEntity<Map<String, String>> updatePassword(@PathVariable("username") String username, @RequestBody Map<String, String> requestBody) {
        String newPassword = requestBody.get("newPassword");
        String oldPassword = requestBody.get("oldPassword");

        Map<String, String> response = new HashMap<>();

        if (newPassword.equals(oldPassword)) { // i have no clue why it is showing up to date when clearly it is not
            response.put("error", "New password cannot be the same as the old password.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean isPasswordUpdated = userService.updatePassword(username, newPassword, oldPassword);
        if (isPasswordUpdated) {
            response.put("message", "Password updated successfully");
            return ResponseEntity.ok(response);
        }
        response.put("error", "User not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PutMapping("update/username/{username}")
    public ResponseEntity<Map<String,String>> updateUsername(@PathVariable("username") String username, @RequestBody Map<String, String> requestBody)
    {
        String newUsername = requestBody.get("newUsername");
        boolean isUsernameUpdated=userService.updateUsername(username,newUsername);//new Password to
        Map<String, String> response = new HashMap<>();
        if (isUsernameUpdated) {
            response.put("message", "Username updated successfully");
            return ResponseEntity.ok(response);
        }
        response.put("error", "User not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PutMapping("update/email/{username}")
    public ResponseEntity<Map<String,String>> updateEmail(@PathVariable("username") String username, @RequestBody Map<String, String> requestBody)
    {
        String newEmail = requestBody.get("newEmail");
        boolean isEmailUpdated=userService.updateEmail(username,newEmail);//new Password to
        Map<String, String> response = new HashMap<>();
        if (isEmailUpdated) {
            response.put("message", "Email updated successfully");
            return ResponseEntity.ok(response);
        }
        response.put("error", "User not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}
