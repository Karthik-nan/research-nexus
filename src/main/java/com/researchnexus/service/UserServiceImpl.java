package com.researchnexus.service;

import com.researchnexus.dto.LoginRequest;
import com.researchnexus.dto.LoginResponse;
import com.researchnexus.dto.UserResponse;
import com.researchnexus.entity.Role;
import com.researchnexus.entity.User;
import com.researchnexus.repository.UserRepository;
import com.researchnexus.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    // CREATE USER (REGISTER)
    @Override
    public UserResponse createUser(User user) {

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole()
        );
    }

    // GET USER BY EMAIL
    @Override
    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    // GET USERS BY NAME
    @Override
    public List<UserResponse> getUserByName(String name) {

        List<User> users = userRepository.findByName(name);

        return users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                ))
                .collect(Collectors.toList());
    }

    // LOGIN (JWT)
    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(token);
    }
}