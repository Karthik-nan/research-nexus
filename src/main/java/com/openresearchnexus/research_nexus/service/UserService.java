package com.openresearchnexus.research_nexus.service;

import com.openresearchnexus.research_nexus.dto.LoginRequest;
import com.openresearchnexus.research_nexus.dto.LoginResponse;
import com.openresearchnexus.research_nexus.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(User user);
    Optional<User> getUserByEmail(String email);
    List<User> getUserByName(String name);
    User saveUser(User user);
    LoginResponse login(LoginRequest request);
}