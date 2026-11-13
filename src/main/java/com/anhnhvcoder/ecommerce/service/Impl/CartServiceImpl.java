package com.anhnhvcoder.ecommerce.service.Impl;

import com.anhnhvcoder.ecommerce.dto.CartDTO;
import com.anhnhvcoder.ecommerce.exception.ResourceNotFoundException;
import com.anhnhvcoder.ecommerce.mapper.CartMapper;
import com.anhnhvcoder.ecommerce.model.Cart;
import com.anhnhvcoder.ecommerce.model.User;
import com.anhnhvcoder.ecommerce.repository.CartRepository;
import com.anhnhvcoder.ecommerce.service.CartService;
import com.anhnhvcoder.ecommerce.service.UserService;
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
        Cart cart = cartRepository.findWithItemsByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
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
        // Only the stored counter is needed - no need to load and map the whole cart.
        User user = userService.getAuthenticatedUser();
        return cartRepository.findTotalItemsByUserId(user.getId()).orElse(0);
    }
}
