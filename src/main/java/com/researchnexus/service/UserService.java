package com.researchnexus.service;

import com.researchnexus.dto.LoginRequest;
import com.researchnexus.dto.LoginResponse;
import com.researchnexus.dto.UserResponse;
import com.researchnexus.entity.User;

import java.util.List;

public interface UserService {

    // REGISTER
    UserResponse createUser(User user);

    // FETCH USER
    UserResponse getUserByEmail(String email);

    List<UserResponse> getUserByName(String name);

    // LOGIN
    LoginResponse login(LoginRequest request);
}