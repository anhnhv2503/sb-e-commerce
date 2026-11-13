package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.request.VerifyEmailRequest;
import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.Impl.EmailService;
import lombok.NonNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Email verification")
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "API verify email", description = "Activate the account with the token from the registration email")
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyEmail(@NonNull @RequestBody VerifyEmailRequest request){

        try {
            return ResponseEntity.ok(new ApiResponse(200, emailService.verifyEmail(request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage()));
        }
    }

}
