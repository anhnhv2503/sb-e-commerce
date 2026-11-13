package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.request.AdminCreateUserRequest;
import com.anhnhvcoder.ecommerce.request.AdminUpdateUserRequest;
import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin - Users", description = "User management (ROLE_ADMIN only)")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "List users", description = "Paged list of users, filterable by keyword (name, email, phone), role and active state")
    @GetMapping
    public ResponseEntity<ApiResponse> getUsers(
            @Parameter(description = "Keyword matched against full name, email and phone")
            @RequestParam(required = false) String search,
            @Parameter(description = "ROLE_USER or ROLE_ADMIN")
            @RequestParam(required = false) String role,
            @Parameter(description = "Filter by email-verified state")
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), adminUserService.getUsers(search, role, active, page, size)));
    }

    @Operation(summary = "Get user detail")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), adminUserService.getUser(userId)));
    }

    @Operation(summary = "Create user", description = "Create an already-activated account with ROLE_USER (default) or ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(HttpStatus.CREATED.value(), adminUserService.createUser(request)));
    }

    @Operation(summary = "Update user profile", description = "Blank fields are left unchanged")
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long userId,
                                                  @Valid @RequestBody AdminUpdateUserRequest request) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), adminUserService.updateUser(userId, request)));
    }

    @Operation(summary = "Lock user", description = "A locked user cannot log in or call authenticated APIs")
    @PutMapping("/{userId}/lock")
    public ResponseEntity<ApiResponse> lockUser(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), adminUserService.setLocked(userId, true)));
    }

    @Operation(summary = "Unlock user")
    @PutMapping("/{userId}/unlock")
    public ResponseEntity<ApiResponse> unlockUser(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), adminUserService.setLocked(userId, false)));
    }

    @Operation(summary = "Activate user", description = "Mark the account's email as verified without sending the verification email")
    @PutMapping("/{userId}/activate")
    public ResponseEntity<ApiResponse> activateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), adminUserService.activateUser(userId)));
    }

    @Operation(summary = "Change user role")
    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse> changeRole(@PathVariable Long userId,
                                                  @Parameter(description = "ROLE_USER or ROLE_ADMIN")
                                                  @RequestParam String roleName) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), adminUserService.changeRole(userId, roleName)));
    }

    @Operation(summary = "Delete user", description = "Only users without orders can be deleted; lock the account otherwise")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User deleted successfully"));
    }
}
