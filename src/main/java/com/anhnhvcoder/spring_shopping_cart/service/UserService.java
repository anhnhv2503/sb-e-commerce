package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.model.User;
import jakarta.mail.MessagingException;

public interface UserService {

    User registerUser(String fullName, String phone, String email, String password, String address) throws MessagingException;

    User getUserByEmail(String email);

    User getUserById(Long id);

    User getAuthenticatedUser();

    User registerAdmin(String fullName, String phone, String email, String password, String address);

    User updateUser(String fullName, String phone, String address);

    User changePassword(String oldPassword, String newPassword);
}