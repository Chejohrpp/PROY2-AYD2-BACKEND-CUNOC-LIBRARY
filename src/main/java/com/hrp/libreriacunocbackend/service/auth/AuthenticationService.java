package com.hrp.libreriacunocbackend.service.auth;

import com.hrp.libreriacunocbackend.dto.auth.AuthenticationRequestDTO;
import com.hrp.libreriacunocbackend.dto.auth.JwtResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;

public interface AuthenticationService {

    JwtResponseDTO authenticateAndGetToken(AuthenticationRequestDTO authDTO) throws EntityNotFoundException;
}
