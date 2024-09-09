package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.model.User;

import java.math.BigDecimal;

public interface CartService {

    Cart getCart(Long id);
    void clearCart(Long id);
    BigDecimal  getTotalPrice(Long id);
    Cart initializeNewCart(User user);
    Cart getCartByUserId(Long userId);
}