package com.hrp.libreriacunocbackend.dto.auth;

import lombok.Value;

@Value
public class AuthenticationRequestDTO {
    private final String username;
    private final String password;
}
