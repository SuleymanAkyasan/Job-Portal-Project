package com.suleyman.jobportal.controller;

import com.suleyman.jobportal.auth.AuthenticationResponse;
import com.suleyman.jobportal.auth.LoginRequest;
import com.suleyman.jobportal.config.JwtService;
import com.suleyman.jobportal.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        final UserDetails userDetails = customUserDetailsService
                .loadUserByUsername(request.getEmail());

        final String jwt = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(AuthenticationResponse.builder().token(jwt).build());
    }
}