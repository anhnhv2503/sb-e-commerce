package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.model.CartItem;

public interface CartItemService {

    Cart addItemToCart(Long cartId, Long productId, int quantity, Long sizeId);
    void removeItemFromCart(Long cartId, Long productId);
    Cart updateItemQuantity(Long cartId, Long productId, int quantity);

    CartItem getCartItem(Long cartId, Long productId);
}
