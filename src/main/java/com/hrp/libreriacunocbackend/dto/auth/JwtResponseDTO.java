package com.hrp.libreriacunocbackend.dto.auth;

import com.hrp.libreriacunocbackend.enums.user.Role;
import lombok.Value;

@Value
public class JwtResponseDTO {
    private final String token;
    private final Role role;
}
