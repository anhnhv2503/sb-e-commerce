package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.dto.CartDTO;
import com.anhnhvcoder.spring_shopping_cart.mapper.CartMapper;
import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.CartRepository;
import com.anhnhvcoder.spring_shopping_cart.service.CartService;
import com.anhnhvcoder.spring_shopping_cart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final CartMapper cartMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public CartDTO getCart() {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId());
        messagingTemplate.convertAndSend("/topic/cart", cart.getCartItems().size());
        return cartMapper.toCartDTO(cart);
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

    @Transactional
    @Override
    public void clearCart() {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId());
        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setTotalItems(0);
        cartRepository.save(cart);
    }

    @Override
    public Integer getCartCount() {
        CartDTO cart = getCart();
        return cart.getTotalItem();
    }
}
