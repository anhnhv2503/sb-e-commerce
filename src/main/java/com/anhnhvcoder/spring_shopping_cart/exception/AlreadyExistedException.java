package com.anhnhvcoder.spring_shopping_cart.exception;

public class AlreadyExistedException extends RuntimeException{
    public AlreadyExistedException(String message) {
        super(message);
    }
}
