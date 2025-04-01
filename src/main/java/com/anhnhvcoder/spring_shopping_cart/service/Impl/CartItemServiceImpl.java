package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.model.CartItem;
import com.anhnhvcoder.spring_shopping_cart.model.Size;
import com.anhnhvcoder.spring_shopping_cart.repository.CartItemRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.CartRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.SizeRepository;
import com.anhnhvcoder.spring_shopping_cart.service.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final SizeRepository sizeRepository;

    @Override
    public Cart addItemToCart(Long cartId, int quantity, Long sizeId) {
        //get cart -> get product -> check if the product already in the cart -> if yes, update quantity - if no add new item
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new RuntimeException("Size not found"));
        Optional<CartItem> isPresentItem = cartItemRepository.findBySizeId(sizeId);
        if(isPresentItem.isPresent()) {
            CartItem existingItem = isPresentItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setTotalPrice(existingItem.getTotalPrice()
                    .add(size.getProduct().getPrice().multiply(new BigDecimal(quantity))));
            cartItemRepository.save(existingItem);
            cart.addToCart(existingItem);
            cart.setTotalItems(cart.getCartItems().size());
            cartRepository.save(cart);
            return cart;
        }else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setSize(size);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(size.getProduct().getPrice());
            cartItem.setTotalPrice(size.getProduct().getPrice().multiply(new BigDecimal(quantity)));
            cartItemRepository.save(cartItem);
            cart.addToCart(cartItem);
            cart.setTotalItems(cart.getCartItems().size());
            cartRepository.save(cart);
            return cart;
        }
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {

    }

    @Override
    public Cart updateItemQuantity(Long cartId, Long productId, int quantity) {
        return null;
    }

    @Override
    public List<CartItem> getCartItems(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }
}
