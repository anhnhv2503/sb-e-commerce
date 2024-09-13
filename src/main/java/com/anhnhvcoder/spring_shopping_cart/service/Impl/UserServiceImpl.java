package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.exception.AlreadyExistedException;
import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.Role;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.RoleRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.UserRepository;
import com.anhnhvcoder.spring_shopping_cart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Override
    public User registerUser(String fullName, String phone, String email, String password, String address) {
        String phoneRegex = "^[0-9]{10}$";
        if (!phone.matches(phoneRegex)) {
            throw new RuntimeException("Invalid phone number. It should contain 10 digits.");
        }
        if (userRepository.findByPhone(phone) != null) {
            throw new AlreadyExistedException("Phone number is already registered");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new AlreadyExistedException("Email is already registered");
        } else {
            User user = new User();
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setAddress(address);
            Role userRole = roleRepository.findByRoleName("ROLE_USER").orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            user.setRoles(Set.of(userRole));
            return userRepository.save(user);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

    @Override
    public User registerAdmin(String fullName, String phone, String email, String password, String address) {
        String phoneRegex = "^[0-9]{10}$";
        if (!phone.matches(phoneRegex)) {
            throw new RuntimeException("Invalid phone number. It should contain 10 digits.");
        }
        if (userRepository.findByPhone(phone) != null) {
            throw new AlreadyExistedException("Phone number is already registered");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new AlreadyExistedException("Email is already registered");
        } else {
            User admin = new User();
            admin.setFullName(fullName);
            admin.setPhone(phone);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setAddress(address);
            Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN").orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            admin.setRoles(Set.of(adminRole));
            return userRepository.save(admin);
        }
    }
}
