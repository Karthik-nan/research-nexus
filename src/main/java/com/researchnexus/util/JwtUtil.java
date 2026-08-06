package com.researchnexus.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET =
            "mysecretkeymysecretkeymysecretkey123456";

    private static final long EXPIRATION_TIME =
            1000 * 60 * 60; // 1 hour

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ========================
    // GENERATE TOKEN
    // ========================
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ========================
    // EXTRACT EMAIL
    // ========================
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // ========================
    // VALIDATE TOKEN
    // ========================
    public boolean validateToken(String token, String email) {

        String tokenEmail = extractEmail(token);

        return tokenEmail.equals(email) && !isTokenExpired(token);
    }

    // ========================
    // CHECK EXPIRATION
    // ========================
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // ========================
    // CLAIMS
    // ========================
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}