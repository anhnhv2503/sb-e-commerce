package com.anhnhvcoder.spring_shopping_cart.config;

import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.Category;
import com.anhnhvcoder.spring_shopping_cart.model.Role;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    @Bean
    ApplicationRunner init(UserRepository userRepository){
        return args -> {
            if(roleRepository.findAll().isEmpty()){
                Role roleUser = new Role();
                roleUser.setRoleName("ROLE_USER");
                roleRepository.save(roleUser);
                Role roleAdmin = new Role();
                roleAdmin.setRoleName("ROLE_ADMIN");
                roleRepository.save(roleAdmin);
                log.warn("Default roles created: ROLE_USER, ROLE_ADMIN");
            }
            if(categoryRepository.findAll().isEmpty()){
                Category c1 = new Category();
                c1.setName("Áo thun");
                categoryRepository.save(c1);
                Category c2 = new Category();
                c2.setName("Áo sơ mi");
                categoryRepository.save(c2);
                Category c3 = new Category();
                c3.setName("Áo khoác");
                categoryRepository.save(c3);
                Category c4 = new Category();
                c4.setName("Quần jean");
                categoryRepository.save(c4);
                Category c5 = new Category();
                c5.setName("Quần kaki");
                categoryRepository.save(c5);
                Category c6 = new Category();
                c6.setName("Quần âu");
                categoryRepository.save(c6);
                Category c7 = new Category();
                c7.setName("Blazer");
                categoryRepository.save(c7);
                log.warn("Default categories created");
            }
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
