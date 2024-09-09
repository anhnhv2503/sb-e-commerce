package com.anhnhvcoder.spring_shopping_cart.repository;

import com.anhnhvcoder.spring_shopping_cart.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);
}
