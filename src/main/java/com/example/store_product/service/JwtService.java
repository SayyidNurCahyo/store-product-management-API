package com.example.store_product.service;

import com.example.store_product.dto.response.JwtClaims;
import com.example.store_product.entity.UserAccount;

public interface JwtService {
    String generateToken(UserAccount account);
    Boolean verifyJwtToken(String token);
    JwtClaims getClaimsByToken(String token);
}
