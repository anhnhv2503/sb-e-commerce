package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.exception.AlreadyExistedException;
import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.exception.TokenExpiredException;
import com.anhnhvcoder.spring_shopping_cart.model.Role;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.RoleRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.UserRepository;
import com.anhnhvcoder.spring_shopping_cart.request.ResetPasswordRequest;
import com.anhnhvcoder.spring_shopping_cart.security.jwt.JwtUtils;
import com.anhnhvcoder.spring_shopping_cart.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;


    @Override
    public User registerUser(String fullName, String phone, String email, String password, String address) throws MessagingException {
        String phoneRegex = "^[0-9]{10}$";
        if (!phone.matches(phoneRegex)) {
            throw new RuntimeException("Invalid phone number. It should contain 10 digits.");
        }
        if (userRepository.findByPhone(phone) != null) {
            throw new AlreadyExistedException("Phone number is already registered");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AlreadyExistedException("Email is already registered");
        } else {
            User user = new User();
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setAddress(address);
            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            user.setRoles(Set.of(userRole));
            user.setActive(false);
            emailService.sendEmail(
                    email,
                    emailService.subjectRegister(),
                    emailService.bodyRegister(email, fullName, phone, address)
            );
            return userRepository.save(user);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AlreadyExistedException("Email is already registered");
        } else {
            User admin = new User();
            admin.setFullName(fullName);
            admin.setPhone(phone);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setAddress(address);
            Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            admin.setRoles(Set.of(adminRole));
            return userRepository.save(admin);
        }
    }

    @Override
    public User updateUser(String fullName, String phone, String address) {
        User user = getAuthenticatedUser();

        user.setFullName(fullName.trim().length() > 0 ? fullName : user.getFullName());
        user.setPhone(phone.trim().length() > 0 ? phone : user.getPhone());
        user.setAddress(address.trim().length() > 0 ? address : user.getAddress());
        log.info("User: {}", user);
        return userRepository.save(user);
    }

    @Override
    public User changePassword(String oldPassword, String newPassword) {
        User user = getAuthenticatedUser();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            return userRepository.save(user);
        }
        throw new ResourceNotFoundException("Old password is incorrect");
    }

    @Override
    public User forgotPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(user == null){
            throw new ResourceNotFoundException("User not found");
        }else if(!user.isActive()){
            throw new ResourceNotFoundException("Email is not active");
        }
        emailService.sendEmail(
                email,
                emailService.subjectResetPassword(),
                emailService.bodyResetPassword(email));

        return user;
    }

    @Override
    public User resetPassword(ResetPasswordRequest request) {
        String email = jwtUtils.getUsernameFromToken(request.getToken());
        Date expDate = jwtUtils.getExpDateFromToken(request.getToken());
        if(!expDate.before(new Date())){
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            if(user == null){
                throw new ResourceNotFoundException("User not found");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            return userRepository.save(user);
        }else{
            throw new TokenExpiredException("Token expired");
        }
    }
}
