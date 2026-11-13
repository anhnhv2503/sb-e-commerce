package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.exception.InactiveUserException;
import com.anhnhvcoder.ecommerce.request.AuthenticationRequest;
import com.anhnhvcoder.ecommerce.response.AuthenticationResponse;
import com.anhnhvcoder.ecommerce.security.jwt.JwtUtils;
import com.anhnhvcoder.ecommerce.service.Impl.AuthenticationService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login with email/password or Google")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthenticationService authenticationService;

    @Operation(summary = "API login", description = "Returns a JWT access token and refresh token")
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
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthenticationResponse(false, "e.getMessage()", null, null));
        }catch (InactiveUserException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthenticationResponse(false, e.getMessage(), null, null));
        }
    }

    @Operation(summary = "API Google login", description = "Exchange a Google OAuth authorization code for a JWT")
    @PostMapping("/google-login")
    public ResponseEntity<AuthenticationResponse> googleLogin(@RequestParam("code") String code){
        return ResponseEntity.ok(authenticationService.googleAuthentication(code));
    }

}
