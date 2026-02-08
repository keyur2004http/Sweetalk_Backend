package com.example.Sweetalk.Util;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.Sweetalk.Enum.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Change this in JwtUtil.java
    private static final String SECRET = "my_super_secret_key_that_is_long_enough_for_256_bits_123456";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final long EXPIRATION_TIME = 1000 * 60 * 60;


    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", "ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateToken(String token, UserDetails userDetails) {
        String usernameFromToken = extractUsername(token);
        return (usernameFromToken.equals(userDetails.getUsername()));
    }
}
