package com.anhnhvcoder.spring_shopping_cart.repository;

import com.anhnhvcoder.spring_shopping_cart.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String name);
}
