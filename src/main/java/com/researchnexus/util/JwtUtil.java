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
            1000 * 60 * 60;

    private Key getSignKey() {

        return Keys.hmacShaKeyFor(
                SECRET.getBytes()
        );
    }

    public String generateToken(
            String email
    ) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(
                        new Date()
                )
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )
                .signWith(
                        getSignKey(),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public String extractEmail(
            String token
    ) {

        return getClaims(
                token
        ).getSubject();
    }

    public boolean validateToken(
            String token
    ) {

        try {

            getClaims(
                    token
            );

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    private Claims getClaims(
            String token
    ) {

        return Jwts.parserBuilder()
                .setSigningKey(
                        getSignKey()
                )
                .build()
                .parseClaimsJws(
                        token
                )
                .getBody();
    }
}