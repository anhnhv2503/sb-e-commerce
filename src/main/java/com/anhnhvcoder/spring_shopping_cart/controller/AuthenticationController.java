package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.exception.InactiveUserException;
import com.anhnhvcoder.spring_shopping_cart.request.AuthenticationRequest;
import com.anhnhvcoder.spring_shopping_cart.response.AuthenticationResponse;
import com.anhnhvcoder.spring_shopping_cart.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtils.generateTokenForUser(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);
            boolean isValid = jwtUtils.verifyToken(token);

            return ResponseEntity
                    .ok(new AuthenticationResponse(isValid, "Login successful", token, refreshToken));
        } catch (UsernameNotFoundException | InactiveUserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthenticationResponse(false, e.getMessage(), null, null));
        }
    }

}
