package com.example.store_product.controller;

import com.example.store_product.dto.request.AuthRequest;
import com.example.store_product.dto.request.RegisterRequest;
import com.example.store_product.dto.response.CommonResponse;
import com.example.store_product.dto.response.LoginResponse;
import com.example.store_product.dto.response.RegisterResponse;
import com.example.store_product.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<?>> register(@RequestBody RegisterRequest request) {
        CommonResponse<RegisterResponse> response = CommonResponse.<RegisterResponse>builder()
                .statusCode(HttpStatus.CREATED.value()).message("Successfully Save Data User")
                .data(authService.register(request)).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<?>> login(@RequestBody AuthRequest request) {
        LoginResponse loginResponse = authService.login(request);
        CommonResponse<LoginResponse> response = CommonResponse.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value()).message("Successfully Login")
                .data(loginResponse).build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(
            value = "/validate-token",
            produces = {"application/json"}
    )
    public ResponseEntity<?> validateToken() {
        boolean valid = this.authService.validateToken();
        CommonResponse response;
        if (valid) {
            response = CommonResponse.builder().statusCode(HttpStatus.OK.value()).message("Successfully Fetch Data").build();
            return ResponseEntity.ok(response);
        } else {
            response = CommonResponse.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).message("Invalid JWT").build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
