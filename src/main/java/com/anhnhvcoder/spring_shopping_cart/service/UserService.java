package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.model.User;

public interface UserService {

    User registerUser(String fullName, String phone, String email, String password, String address);

    User getUserByEmail(String email);

    User getUserById(Long id);

    User getAuthenticatedUser();
}