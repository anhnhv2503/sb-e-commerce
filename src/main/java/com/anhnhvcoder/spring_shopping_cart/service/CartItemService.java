package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.model.CartItem;

import java.util.List;

public interface CartItemService {

    Cart addItemToCart(Long cartId, int quantity, Long sizeId);
    void removeItemFromCart(Long cartId, Long productId);
    Cart updateItemQuantity(Long cartId, Long productId, int quantity);
    List<CartItem> getCartItems(Long cartId);
}
