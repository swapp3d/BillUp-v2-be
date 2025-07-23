package com.example.BillUp.config.jwt;

import com.example.BillUp.entities.Token;
import com.example.BillUp.entities.User;
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
                      @Value("${auth.jwt.refresh-expiration-ms}") long refreshExpirationMs, TokenRepository tokenRepository) {
        this.SECRET_KEY = secretKey;
        this.EXPIRATION_MS = expirationMs;
        this.REFRESH_EXPIRATION_MS = refreshExpirationMs;
        this.tokenRepository = tokenRepository;
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .claim("userId", user.getId())
                .claim("roles", user.getRole().name())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .claim("userId", user.getId())
                .claim("roles", user.getRole().name())
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
           boolean matchesUsers = email.equals(user.getEmail());
           boolean notExpired = !isExpired(token);

            Optional<Token> storedToken = tokenRepository.findByToken(token);
            boolean notRevoked = storedToken.isPresent() && !storedToken.get().isRevoked();

            return matchesUsers && notExpired && notRevoked;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration == null || expiration.before(new Date());
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmailExpired(String token) {
        return extractClaimAllowExpired(token, Claims::getSubject);
    }

    private <T> T extractClaimAllowExpired(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaimsAllowExpired(token);
        if (claims == null) {
            return null;
        }
        return resolver.apply(claims);
    }

    public LocalDateTime extractExpirationDate(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        if (expiration == null) {
            return null;
        }
        return expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        System.out.println("extracted claims: " + claims);
        if (claims == null) {
            return null;
        }
        return resolver.apply(claims);
    }

    private Claims extractAllClaimsAllowExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            System.out.println("token on extract all claims: " + token);
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("exception occurred, null");
            return null;
        }
    }
}
