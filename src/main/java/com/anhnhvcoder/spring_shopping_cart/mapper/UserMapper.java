package com.anhnhvcoder.spring_shopping_cart.mapper;

import com.anhnhvcoder.spring_shopping_cart.dto.UserDTO;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setPhone(user.getPhone());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setRole(user.getRoles().stream().findFirst().get().getRoleName());
        return userDTO;
    }
}
