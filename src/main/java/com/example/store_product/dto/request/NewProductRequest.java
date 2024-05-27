package com.example.store_product.dto.request;

import com.example.store_product.dto.response.ImageResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewProductRequest {
    @NotBlank(message = "Password is Required")
    private String sku;
    @NotBlank(message = "Password is Required")
    private String name;
    @NotNull(message = "Password is Required")
    @Min(value = 1, message = "Quantity Should be Greater Than 0")
    private Integer quantity;
    @NotNull(message = "Price is Required")
    @Min(value = 1, message = "Price Should be Greater Than 0")
    private Long price;
    @NotNull(message = "Image is Required")
    private MultipartFile image;
}
