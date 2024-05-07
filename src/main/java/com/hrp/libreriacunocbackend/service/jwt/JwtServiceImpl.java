package com.hrp.libreriacunocbackend.service.jwt;

import com.hrp.libreriacunocbackend.entities.user.User;
import com.hrp.libreriacunocbackend.enums.user.Role;
import com.hrp.libreriacunocbackend.repository.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtServiceImpl implements JwtService {

    public static final String SECRET_KEY ="bmpJGkpYz0Af4ub65tzlnPRX2De1o02uuStUt2y1nhgAXzhngZJtWOgVAlOWYD41";

    private final UserRepository userRepository;

    @Autowired
    public JwtServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public String generateToken(String username, Role role) {
        return Jwts.builder()
                .claims(Collections.singletonMap("role", role))
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + 1800000))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public String getUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    @Override
    public String getPayload(String token) {
        Claims claims = extractClaims(token);
        return claims.toString();
    }

    @Override
    public boolean isValid(String token) {
        Claims claims = extractClaims(token);
        Date expirationDate = claims.getExpiration();
        return new Date().before(expirationDate);
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public void updateTokenExpiration(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        // TODO validation
        User user = userOpt.get();
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

    }

    @Override
    public boolean isTokenExpired(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        // TODO validation
        User user = userOpt.get();
        return user.getTokenExpiration() == null
                || LocalDateTime.now().isAfter(user.getTokenExpiration());
    }
}
