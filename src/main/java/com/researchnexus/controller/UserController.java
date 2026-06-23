package com.researchnexus.controller;

import com.researchnexus.dto.LoginRequest;
import com.researchnexus.dto.LoginResponse;
import com.researchnexus.dto.UserResponse;
import com.researchnexus.entity.User;
import com.researchnexus.repository.UserRepository;
import com.researchnexus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // ---------------- REGISTER ----------------
    @PostMapping("/register")
    public UserResponse register(@RequestBody User user) {

        User savedUser = userService.createUser(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse response = userService.login(request);

        return ResponseEntity.ok(response);
    }

    // ---------------- GET BY EMAIL ----------------
    @GetMapping("/email")
    public Optional<User> getByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }

    // ---------------- GET BY NAME ----------------
    @GetMapping("/name")
    public List<User> getByName(@RequestParam String name) {
        return userService.getUserByName(name);
    }

    // ---------------- CURRENT USER (/me) ----------------
    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    // ---------------- USER ACCESS ----------------
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/user")
    public String userAccess() {
        return "USER access granted";
    }

    // ---------------- ADMIN ACCESS ----------------
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public String adminAccess() {
        return "ADMIN access granted";
    }
}