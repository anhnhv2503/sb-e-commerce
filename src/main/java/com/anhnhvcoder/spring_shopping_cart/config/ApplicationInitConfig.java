package com.anhnhvcoder.spring_shopping_cart.config;

import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.Role;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.RoleRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Bean
    ApplicationRunner init(UserRepository userRepository){
        return args -> {
            if(userRepository.findByEmail("prjonlineshop@gmail.com") == null){
                User user = new User();
                user.setFullName("AD Văn MIN");
                user.setPhone("0999999999");
                user.setAddress("Việt Nam");
                user.setEmail("prjonlineshop@gmail.com");
                user.setPassword(passwordEncoder.encode("123456"));
                Role userRole = roleRepository.findByRoleName("ROLE_ADMIN")
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
                user.setRoles(Set.of(userRole));
                user.setActive(true);
                userRepository.save(user);
                log.warn("Default user created: admin");
            }
        };
    }
}
