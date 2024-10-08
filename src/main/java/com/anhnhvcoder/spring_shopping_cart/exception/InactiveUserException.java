package com.anhnhvcoder.spring_shopping_cart.exception;

public class InactiveUserException extends RuntimeException{
    public InactiveUserException(String message) {
        super(message);
    }
}
