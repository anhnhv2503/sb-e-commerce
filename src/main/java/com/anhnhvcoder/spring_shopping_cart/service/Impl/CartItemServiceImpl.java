package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.model.CartItem;
import com.anhnhvcoder.spring_shopping_cart.model.Size;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.CartItemRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.CartRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.SizeRepository;
import com.anhnhvcoder.spring_shopping_cart.service.CartItemService;
import com.anhnhvcoder.spring_shopping_cart.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Cart addItemToCart(Long cartId, int quantity, Long sizeId) {
        //get cart -> get product -> check if the product already in the cart -> if yes, update quantity - if no add new item
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new RuntimeException("Size not found"));
        if(quantity > size.getQuantity()) throw new ResourceNotFoundException("Quantity is greater than size quantity");
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
            messagingTemplate.convertAndSend("/topic/cart", cart.getCartItems().size());
            log.info("Added cart to cart : {}", cart.getCartItems().size());
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
            messagingTemplate.convertAndSend("/topic/cart", cart.getCartItems().size());
            log.info("Added cart to cart : {}", cart.getCartItems().size());
            return cart;
        }
    }

    @Transactional
    @Override
    public void removeItemFromCart(Long itemId) {
        User user = userService.getAuthenticatedUser();
        CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        Cart cart = cartRepository.findById(cartItem.getCart().getId()).orElseThrow(() -> new RuntimeException("Cart not found"));
        if(!user.getId().equals(cart.getUser().getId())) throw new ResourceNotFoundException("You do not have permission to delete this item");
        cart.getCartItems().remove(cartItem);
        cartItem.setCart(null);
        cart.setTotalItems(cart.getCartItems().size());
        cart.updateTotalPrice();
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
        messagingTemplate.convertAndSend("/topic/cart", cart.getCartItems().size());
        log.info("Added cart to cart : {}", cart.getCartItems().size());
    }

    @Override
    public void updateQuantity(Long itemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if(quantity > cartItem.getSize().getQuantity()) throw new ResourceNotFoundException(cartItem.getSize().getProduct().getName() + " chỉ còn " + cartItem.getSize().getQuantity());
        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getSize().getProduct().getPrice().multiply(new BigDecimal(quantity)));
        Cart cart = cartRepository.findById(cartItem.getCart().getId()).get();
        cart.setTotalItems(cart.getCartItems().size());
        cart.updateTotalPrice();
        cartRepository.save(cart);
        cartItemRepository.save(cartItem);
    }
}
