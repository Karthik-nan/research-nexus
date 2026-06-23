package com.researchnexus.service;

import com.researchnexus.dto.LoginRequest;
import com.researchnexus.dto.LoginResponse;
import com.researchnexus.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(User user);
    Optional<User> getUserByEmail(String email);
    List<User> getUserByName(String name);
    User saveUser(User user);
    LoginResponse login(LoginRequest request);
}