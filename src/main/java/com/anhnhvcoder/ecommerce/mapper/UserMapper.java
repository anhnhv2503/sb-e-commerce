package com.anhnhvcoder.ecommerce.mapper;

import com.anhnhvcoder.ecommerce.dto.UserDTO;
import com.anhnhvcoder.ecommerce.model.Role;
import com.anhnhvcoder.ecommerce.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setPhone(user.getPhone());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setRole(user.getRoles().stream().findFirst().map(Role::getRoleName).orElse(null));
        userDTO.setActive(user.isActive());
        userDTO.setLocked(user.isLockedByAdmin());
        userDTO.setCreatedAt(user.getCreatedAt());
        return userDTO;
    }
}
