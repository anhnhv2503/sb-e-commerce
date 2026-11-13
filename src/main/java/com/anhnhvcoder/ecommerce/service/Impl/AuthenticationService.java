package com.anhnhvcoder.ecommerce.service.Impl;

import com.anhnhvcoder.ecommerce.client.GoogleIdentityClient;
import com.anhnhvcoder.ecommerce.client.GoogleUserClient;
import com.anhnhvcoder.ecommerce.exception.InactiveUserException;
import com.anhnhvcoder.ecommerce.exception.ResourceNotFoundException;
import com.anhnhvcoder.ecommerce.model.User;
import com.anhnhvcoder.ecommerce.repository.RoleRepository;
import com.anhnhvcoder.ecommerce.repository.UserRepository;
import com.anhnhvcoder.ecommerce.request.ExchangeTokenRequest;
import com.anhnhvcoder.ecommerce.response.AuthenticationResponse;
import com.anhnhvcoder.ecommerce.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
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
                () -> {
                    User newUser = new User();
                    newUser.setEmail(userInfo.getEmail());
                    newUser.setEmail(userInfo.getEmail());
                    newUser.setRoles(Set.of(roles));
                    newUser.setActive(true);
                    newUser.setAddress(userInfo.getFamilyName() + " address");
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    return userRepository.save(newUser);
                }
        );

        if (user.isLockedByAdmin()) {
            throw new InactiveUserException("Your account has been locked. Please contact the administrator.");
        }

        String token = jwtUtils.generateTokenForGoogleUser(user);
        String refreshToken = jwtUtils.generateTokenForGoogleUser(user);
        boolean isValid = jwtUtils.verifyToken(token);

        return new AuthenticationResponse(isValid, "Login successful", token, refreshToken);

    }
}
