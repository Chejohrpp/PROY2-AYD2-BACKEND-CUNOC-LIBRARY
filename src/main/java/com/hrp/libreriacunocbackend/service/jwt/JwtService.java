package com.hrp.libreriacunocbackend.service.jwt;

import com.hrp.libreriacunocbackend.enums.user.Role;

public interface JwtService {
    String generateToken(String username, Role role);

    String getUsername(String token);

    String getPayload(String token);

    boolean isValid(String token);

    void updateTokenExpiration(String username);

    public boolean isTokenExpired(String username);
}
