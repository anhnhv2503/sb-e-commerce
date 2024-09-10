package com.anhnhvcoder.spring_shopping_cart.repository;

import com.anhnhvcoder.spring_shopping_cart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{

    User findByEmail(String email);

    User findByPhone(String phone);
}
