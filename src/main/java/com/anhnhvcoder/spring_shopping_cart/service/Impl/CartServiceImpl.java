package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.dto.CartDTO;
import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.mapper.CartMapper;
import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.model.CartItem;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.CartItemRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.CartRepository;
import com.anhnhvcoder.spring_shopping_cart.service.CartService;
import com.anhnhvcoder.spring_shopping_cart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final CartMapper cartMapper;

    @Override
    public CartDTO getCart() {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId());
        return cartMapper.toCartDTO(cart);
    }

    @Transactional
    @Override
    public void clearCart(Long id) {
//        Cart cart = getCart(id);
//        cartItemRepository.deleteAllByCartId(cart.getId());
//        cart.getCartItems().clear();
//        cartRepository.deleteById(id);
//        cart.setTotalPrice(BigDecimal.ZERO);
//        cart.setTotalItems(0);
    }

    @Override
    public BigDecimal getTotalPrice(Long id) {
//        Cart cart = getCart(id);
//        return cart.getTotalPrice();
        return null;
    }

    @Override
    public Cart initializeNewCart(User user) {
       return Optional
               .ofNullable(getCartByUserId(user.getId()))
               .orElseGet(()->{
           Cart cart = new Cart();
           cart.setUser(user);
           return cartRepository.save(cart);
       });
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}
