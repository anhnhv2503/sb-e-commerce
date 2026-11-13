package com.anhnhvcoder.ecommerce.security.user;

import com.anhnhvcoder.ecommerce.exception.InactiveUserException;
import com.anhnhvcoder.ecommerce.exception.ResourceNotFoundException;
import com.anhnhvcoder.ecommerce.model.User;
import com.anhnhvcoder.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Email"));
        if(user.isLockedByAdmin()){
            throw new InactiveUserException("Your account has been locked. Please contact the administrator.");
        }
        if(!user.isActive()){
            throw new InactiveUserException("Account is not activated yet! Please check your email to activate your account.");
        }
        return ShopUserDetails.buildUserDetails(user);
    }
}
