package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.exception.AlreadyExistedException;
import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.UserRepository;
import com.anhnhvcoder.spring_shopping_cart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User registerUser(String fullName, String phone, String email, String password, String address) {
        if(userRepository.findByEmail(email) == null){
            User user = new User();
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setAddress(address);
//            user.setRoles;
            return userRepository.save(user);
        }else{
            throw new AlreadyExistedException("Email is already existed");
        }
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
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
}
