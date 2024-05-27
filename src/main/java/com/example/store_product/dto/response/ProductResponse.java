package com.example.store_product.dto.response;

import com.example.store_product.entity.Image;
import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String sku;
    private String name;
    private Integer quantity;
    private Long price;
    private ImageResponse image;
}
