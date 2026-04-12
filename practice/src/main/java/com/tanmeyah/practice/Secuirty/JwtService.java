package com.tanmeyah.practice.Secuirty;

import com.tanmeyah.practice.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String ROLE_CLAIM = "role";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    // Every JWT token has claims.
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim(ROLE_CLAIM, user.getRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        if (userDetails instanceof User user) {
            return generateToken(user);
        }
        // Fallback: keep old behavior if principal isn't our User entity.
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final Claims claims = extractAllClaims(token);
            if (claims.getExpiration().before(new Date())) {
                return false;
            }
            final String subject = claims.getSubject();
            boolean matchesIdentity;
            if (userDetails instanceof User user) {
                matchesIdentity = subject.equals(user.getId().toString());
                String roleClaim = claims.get(ROLE_CLAIM, String.class);
                if (roleClaim != null && !roleClaim.equals(user.getRole().name())) {
                    return false;
                }
            } else {
                matchesIdentity = subject.equals(userDetails.getUsername());
            }
            return matchesIdentity;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is missing. Set jwt.secret in application.yaml or JWT_SECRET env var.");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
