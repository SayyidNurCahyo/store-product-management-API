package com.example.store_product.service;

import com.example.store_product.dto.request.NewProductRequest;
import com.example.store_product.dto.request.SearchProductRequest;
import com.example.store_product.dto.request.UpdateProductRequest;
import com.example.store_product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductResponse addProduct(NewProductRequest menu);
    ProductResponse getProductById(String id);
    Page<ProductResponse> getAllProduct(SearchProductRequest request);
    ProductResponse updateProduct(UpdateProductRequest menu);
    ProductResponse deleteById(String id);
}
