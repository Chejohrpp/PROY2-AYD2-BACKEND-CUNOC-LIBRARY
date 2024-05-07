package com.hrp.libreriacunocbackend.service.auth;

import com.hrp.libreriacunocbackend.dto.auth.AuthenticationRequestDTO;
import com.hrp.libreriacunocbackend.dto.auth.JwtResponseDTO;
import com.hrp.libreriacunocbackend.entities.user.User;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.repository.user.UserRepository;
import com.hrp.libreriacunocbackend.service.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private UserRepository userRepository;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public JwtResponseDTO authenticateAndGetToken(AuthenticationRequestDTO authDTO) throws EntityNotFoundException {
        User userToAuth = userRepository.findByUsername(authDTO.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %s not found", authDTO.getUsername())));

        UsernamePasswordAuthenticationToken authData
                = new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authData);
            if (authentication.isAuthenticated()) {
                jwtService.updateTokenExpiration(authDTO.getUsername());
                return new JwtResponseDTO(jwtService.generateToken(authDTO.getUsername(), userToAuth.getRole()), userToAuth.getRole());
            }
        } catch (AuthenticationException ex) {
            log.error("Error authenticating", ex);
        }
        throw new UsernameNotFoundException("Invalid user request");
    }
}
