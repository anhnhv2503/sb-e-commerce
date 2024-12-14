package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.client.GoogleIdentityClient;
import com.anhnhvcoder.spring_shopping_cart.client.GoogleUserClient;
import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.RoleRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.UserRepository;
import com.anhnhvcoder.spring_shopping_cart.request.ExchangeTokenRequest;
import com.anhnhvcoder.spring_shopping_cart.response.AuthenticationResponse;
import com.anhnhvcoder.spring_shopping_cart.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final GoogleIdentityClient googleIdentityClient;
    private final GoogleUserClient googleUserClient;

    @NonFinal
    @Value("${gg.identity.clientId}")
    protected String CLIENT_ID;
    @NonFinal
    @Value("${gg.identity.clientSecret}")
    protected String CLIENT_SECRET;
    @NonFinal
    @Value("${gg.identity.redirectUri}")
    protected String REDIRECT_URI;
    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse googleAuthentication(String code) {
        var response = googleIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .redirectUri(REDIRECT_URI)
                        .grantType(GRANT_TYPE)
                .build());

        var userInfo = googleUserClient.getUserInfo("json", response.getAccessToken());
        var roles = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        var user = userRepository.findByEmail(userInfo.getEmail()).orElseGet(
                () -> userRepository.save(User.builder()
                                .fullName(userInfo.getName())
                                .email(userInfo.getEmail())
                                .roles(Set.of(roles))
                                .active(true)
                                .address(userInfo.getFamilyName() + " address")
                                .phone("")
                                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .build())
        );

        String token = jwtUtils.generateTokenForGoogleUser(user);
        String refreshToken = jwtUtils.generateTokenForGoogleUser(user);
        boolean isValid = jwtUtils.verifyToken(token);

        return new AuthenticationResponse(isValid, "Login successful", token, refreshToken);

    }
}
