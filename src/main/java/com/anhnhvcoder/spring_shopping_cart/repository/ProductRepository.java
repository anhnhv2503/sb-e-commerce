package com.anhnhvcoder.spring_shopping_cart.repository;

import com.anhnhvcoder.spring_shopping_cart.model.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByBrand(String brand);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    List<Product> findByName(String name);

    @Query("SELECT DISTINCT p.brand FROM Product p")
    List<String> findDistinctBrand();

}
