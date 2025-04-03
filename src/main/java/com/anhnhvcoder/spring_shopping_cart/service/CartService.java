package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.dto.CartDTO;
import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.model.User;

public interface CartService {

    CartDTO getCart();
    Cart initializeNewCart(User user);
    Cart getCartByUserId(Long userId);
    void clearCart();
}