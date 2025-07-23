package com.example.BillUp.config.jwt;

import com.example.BillUp.dto.authentication.TokenValidationResult;
import com.example.BillUp.entities.Token;
import com.example.BillUp.entities.User;
import com.example.BillUp.exceptions.InvalidJwtException;
import com.example.BillUp.repositories.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {
    private final String SECRET_KEY;
    private final long EXPIRATION_MS;
    private final long REFRESH_EXPIRATION_MS;
    private final TokenRepository tokenRepository;

    public JwtService(@Value("${auth.jwt.secret.key}") String secretKey,
                      @Value("${auth.jwt.expiration-ms}") long expirationMs,
                      @Value("${auth.jwt.refresh-expiration-ms}") long refreshExpirationMs,
                      TokenRepository tokenRepository) {
        this.SECRET_KEY = secretKey;
        this.EXPIRATION_MS = expirationMs;
        this.REFRESH_EXPIRATION_MS = refreshExpirationMs;
        this.tokenRepository = tokenRepository;
    }

    public String generateToken(User user) {
        return buildToken(user, EXPIRATION_MS);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, REFRESH_EXPIRATION_MS);
    }

    private String buildToken(User user, long expirationTimeMs) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .claim("userId", user.getId())
                .claim("roles", user.getRole().name())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenRevoked(String token) {
        Optional<Token> storedToken = tokenRepository.findByToken(token);
        Token t = storedToken.orElse(null);
        return t == null || t.isRevoked();
    }

    public TokenValidationResult validateToken(String token, User user) {
        String email = extractEmail(token);
        boolean matchesUser = email.equals(user.getEmail());
        boolean expired = isExpired(token);
        boolean revoked = isTokenRevoked(token);
        boolean isValid = matchesUser && !revoked && !expired;

        return new TokenValidationResult(isValid, revoked, expired, email);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public LocalDateTime extractExpirationDate(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private boolean isExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException("Invalid JWT token");
        }
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
