package com.app.coursewagon.controller;


import com.app.coursewagon.entity.User;
import com.app.coursewagon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestParam String email, @RequestParam String password) {
        try {
            User newUser = userService.registerUser(email, password);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String uid) {
        Optional<User> user = userService.authenticateUser(uid);
        return ResponseEntity.ok(user);
    }
}