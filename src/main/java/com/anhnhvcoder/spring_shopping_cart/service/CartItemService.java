package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.model.CartItem;

import java.util.List;

public interface CartItemService {

    Cart addItemToCart(Long cartId, int quantity, Long sizeId);
    void removeItemFromCart(Long itemId);
    void updateQuantity(Long itemId, int quantity);
}
