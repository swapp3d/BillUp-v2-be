package com.example.BillUp.config.jwt;

import com.example.BillUp.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {
    private final String SECRET_KEY;
    private final long EXPIRATION_MS;
    private final long REFRESH_EXPIRATION_MS;

    public JwtService(@Value("${auth.jwt.secret.key}") String secretKey,
                      @Value("${auth.jwt.expiration-ms}") long expirationMs,
                      @Value("${auth.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.SECRET_KEY = secretKey;
        this.EXPIRATION_MS = expirationMs;
        this.REFRESH_EXPIRATION_MS = refreshExpirationMs;
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roles", List.of(user.getRole().name()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId",  user.getId())
                .claim("roles",   List.of(user.getRole().name()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_MS))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public boolean isTokenValid(String token, User user) {
        try {
            String email = extractEmail(token);
            return email.equals(user.getEmail()) && !isExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
