package com.example.store_product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    @NotBlank(message = "Email or Phone Number is Required")
    private String identifier;
    @NotBlank(message = "Password is Required")
    private String password;
}
