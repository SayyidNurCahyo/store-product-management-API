package com.example.store_product.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProductRequest {
    private Integer size;
    private Integer page;
    private String sortBy;
    private String direction;
    private String name;
}
