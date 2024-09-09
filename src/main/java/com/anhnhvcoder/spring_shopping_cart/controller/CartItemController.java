package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.CartItemRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.UserRepository;
import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.CartItemService;
import com.anhnhvcoder.spring_shopping_cart.service.CartService;
import com.anhnhvcoder.spring_shopping_cart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/cart-item")
@RequiredArgsConstructor
public class CartItemController {
    
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final UserService userService;

    @PostMapping("/add-item")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestParam("productId") Long productId,
                                                     @RequestParam("quantity") int quantity){
        try {
            User user = userService.getUserById(1L);
            Cart cart = cartService.initializeNewCart(user);
            cartItemService.addItemToCart(cart.getId(), productId, quantity);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Item added to cart"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new ApiResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/remove-item/{cartId}/{productId}")
    public ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable Long cartId,
                                                          @PathVariable Long productId){
        try {
            cartItemService.removeItemFromCart(cartId, productId);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Item removed from cart"));
        }catch (ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new ApiResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
        }
    }

    @PutMapping("/cart/{cartId}/item/{productId}/update")
    public ResponseEntity<ApiResponse> updateItemQuantity(@PathVariable Long cartId,
                                                          @PathVariable Long productId,
                                                          @RequestParam("quantity") int quantity){
        try {
            cartItemService.updateItemQuantity(cartId, productId, quantity);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Item quantity updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new ApiResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }
}
