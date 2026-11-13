package com.anhnhvcoder.ecommerce.service.Impl;

import com.anhnhvcoder.ecommerce.dto.UserDTO;
import com.anhnhvcoder.ecommerce.exception.AlreadyExistedException;
import com.anhnhvcoder.ecommerce.exception.ResourceNotFoundException;
import com.anhnhvcoder.ecommerce.mapper.UserMapper;
import com.anhnhvcoder.ecommerce.model.Cart;
import com.anhnhvcoder.ecommerce.model.Role;
import com.anhnhvcoder.ecommerce.model.User;
import com.anhnhvcoder.ecommerce.repository.CartRepository;
import com.anhnhvcoder.ecommerce.repository.OrderRepository;
import com.anhnhvcoder.ecommerce.repository.RoleRepository;
import com.anhnhvcoder.ecommerce.repository.UserRepository;
import com.anhnhvcoder.ecommerce.request.AdminCreateUserRequest;
import com.anhnhvcoder.ecommerce.request.AdminUpdateUserRequest;
import com.anhnhvcoder.ecommerce.service.AdminUserService;
import com.anhnhvcoder.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private static final Set<String> SUPPORTED_ROLES = Set.of("ROLE_USER", "ROLE_ADMIN");
    private static final int MAX_PAGE_SIZE = 100;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public Page<UserDTO> getUsers(String search, String roleName, Boolean active, int page, int size) {
        int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(Math.max(page, 0), pageSize, Sort.by(Sort.Direction.DESC, "id"));
        String keyword = StringUtils.hasText(search) ? search.trim() : null;
        String role = StringUtils.hasText(roleName) ? normalizeRole(roleName) : null;
        return userRepository.searchUsers(keyword, role, active, pageable).map(userMapper::toUserDTO);
    }

    @Override
    public UserDTO getUser(Long userId) {
        return userMapper.toUserDTO(findUser(userId));
    }

    @Override
    public UserDTO createUser(AdminCreateUserRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AlreadyExistedException("Phone number is already registered");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistedException("Email is already registered");
        }
        String roleName = StringUtils.hasText(request.getRoleName()) ? request.getRoleName() : "ROLE_USER";

        User user = new User();
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(new HashSet<>(Set.of(findRole(roleName))));
        // Accounts created by an admin skip email verification.
        user.setActive(true);
        user.setLocked(false);
        return userMapper.toUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateUser(Long userId, AdminUpdateUserRequest request) {
        User user = findUser(userId);
        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName().trim());
        }
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            User phoneOwner = userRepository.findByPhone(request.getPhone());
            if (phoneOwner != null && !phoneOwner.getId().equals(userId)) {
                throw new AlreadyExistedException("Phone number is already registered");
            }
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getAddress())) {
            user.setAddress(request.getAddress().trim());
        }
        return userMapper.toUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO setLocked(Long userId, boolean locked) {
        User user = findUser(userId);
        if (locked) {
            preventSelfAction(user, "lock");
        }
        user.setLocked(locked);
        log.info("Admin set locked={} for user id {}", locked, user.getId());
        return userMapper.toUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO activateUser(Long userId) {
        User user = findUser(userId);
        user.setActive(true);
        return userMapper.toUserDTO(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDTO changeRole(Long userId, String roleName) {
        User user = findUser(userId);
        preventSelfAction(user, "change the role of");
        Role role = findRole(roleName);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userMapper.toUserDTO(userRepository.save(user));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = findUser(userId);
        preventSelfAction(user, "delete");
        if (orderRepository.existsByUserId(userId)) {
            throw new IllegalStateException("User has orders and cannot be deleted. Lock the account instead.");
        }
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cartRepository.delete(cart); // cart items are removed by the cascade
        }
        user.setRoles(new HashSet<>());
        userRepository.delete(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Role findRole(String roleName) {
        String normalized = normalizeRole(roleName);
        if (!SUPPORTED_ROLES.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported role: " + roleName);
        }
        return roleRepository.findByRoleName(normalized)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    private String normalizeRole(String roleName) {
        String upper = roleName.trim().toUpperCase();
        return upper.startsWith("ROLE_") ? upper : "ROLE_" + upper;
    }

    private void preventSelfAction(User target, String action) {
        User current = userService.getAuthenticatedUser();
        if (Objects.equals(current.getId(), target.getId())) {
            throw new IllegalArgumentException("You cannot " + action + " your own account");
        }
    }
}
