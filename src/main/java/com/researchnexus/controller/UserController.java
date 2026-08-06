package com.researchnexus.controller;

import com.researchnexus.dto.LoginRequest;
import com.researchnexus.dto.LoginResponse;
import com.researchnexus.dto.UserResponse;
import com.researchnexus.entity.User;
import com.researchnexus.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // REGISTER USER
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    // LOGIN USER
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // GET USER BY EMAIL
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // GET USERS BY NAME
    @GetMapping("/name/{name}")
    public ResponseEntity<List<UserResponse>> getByName(@PathVariable String name) {
        return ResponseEntity.ok(userService.getUserByName(name));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {


        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        String email = authentication.getName();


        return ResponseEntity.ok(
                userService.getUserByEmail(email)
        );

    }

}