package com.example.store_product.service;

import com.example.store_product.dto.request.AuthRequest;
import com.example.store_product.dto.request.RegisterRequest;
import com.example.store_product.dto.response.LoginResponse;
import com.example.store_product.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(AuthRequest request);
    boolean validateToken();
}
