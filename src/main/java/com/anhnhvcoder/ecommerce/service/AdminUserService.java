package com.anhnhvcoder.ecommerce.service;

import com.anhnhvcoder.ecommerce.dto.UserDTO;
import com.anhnhvcoder.ecommerce.request.AdminCreateUserRequest;
import com.anhnhvcoder.ecommerce.request.AdminUpdateUserRequest;
import org.springframework.data.domain.Page;

public interface AdminUserService {

    Page<UserDTO> getUsers(String search, String roleName, Boolean active, int page, int size);

    UserDTO getUser(Long userId);

    UserDTO createUser(AdminCreateUserRequest request);

    UserDTO updateUser(Long userId, AdminUpdateUserRequest request);

    UserDTO setLocked(Long userId, boolean locked);

    UserDTO activateUser(Long userId);

    UserDTO changeRole(Long userId, String roleName);

    void deleteUser(Long userId);
}
