package com.researchnexus.security;

import com.researchnexus.service.CustomUserDetailsService;
import com.researchnexus.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return request.getMethod().equalsIgnoreCase("OPTIONS")
                || path.equals("/api/users/login")
                || path.equals("/api/users/register");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {



        String authHeader = request.getHeader("Authorization");

        System.out.println(
                "Authorization header present: " +
                        (authHeader != null)
        );

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            System.out.println("JWT NOT FOUND");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            String email = jwtUtil.extractEmail(token);

            System.out.println(
                    "JWT email: " + email
            );

            if (email != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                boolean valid =
                        jwtUtil.validateToken(token, email);

                System.out.println(
                        "JWT valid: " + valid
                );

                if (valid) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println(
                            "AUTHENTICATION SET: " +
                                    userDetails.getUsername()
                    );

                    System.out.println(
                            "AUTHORITIES: " +
                                    userDetails.getAuthorities()
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT ERROR: " +
                            e.getClass().getSimpleName() +
                            " - " +
                            e.getMessage()
            );

            SecurityContextHolder.clearContext();
        }

        System.out.println(
                "FINAL AUTHENTICATION: " +
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
        );

        System.out.println(
                "================================"
        );

        filterChain.doFilter(request, response);
    }
}