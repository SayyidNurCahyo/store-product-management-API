package com.example.store_product.service.impl;

import com.example.store_product.dto.request.NewProductRequest;
import com.example.store_product.dto.request.SearchProductRequest;
import com.example.store_product.dto.request.UpdateProductRequest;
import com.example.store_product.dto.response.ImageResponse;
import com.example.store_product.dto.response.ProductResponse;
import com.example.store_product.entity.Image;
import com.example.store_product.entity.Product;
import com.example.store_product.repository.ProductRepository;
import com.example.store_product.service.ImageService;
import com.example.store_product.service.ProductService;
import com.example.store_product.util.ValidationUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ValidationUtil validationUtil;
    private final ImageService imageService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse addProduct(NewProductRequest request) {
        validationUtil.validate(request);
        if (request.getImage().isEmpty()) throw new ConstraintViolationException("Product Image is Required", null);
        Product product = Product.builder().name(request.getName()).price(request.getPrice())
                .sku(request.getSku()).quantity(request.getQuantity()).build();
        Image imageAdded = imageService.addImage(request.getImage());
        product.setImage(imageAdded);
        Product productSaved = productRepository.saveAndFlush(product);
        return convertToProductResponse(productSaved);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product Not Found"));
        return convertToProductResponse(product);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> getAllProduct(SearchProductRequest request) {
        if (request.getPage()<1) request.setPage(1);
        if (request.getSize()<1) request.setSize(1);
        Pageable page = PageRequest.of(request.getPage() -1, request.getSize(), Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));
        if(request.getName()!=null){
            Page<Product> products = productRepository.findProduct("%"+request.getName()+"%", page);
            return convertToPageProductResponse(products);
        }else {
            return convertToPageProductResponse(productRepository.findAll(page));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse updateProduct(UpdateProductRequest request) {
        validationUtil.validate(request);
        Product product = productRepository.findById(request.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product Not Found"));
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setQuantity(request.getQuantity());
        if(request.getImage()!=null){
            Image image = product.getImage();
            Image imageNew = imageService.addImage(request.getImage());
            product.setImage(imageNew);
            productRepository.saveAndFlush(product);
            imageService.delete(image);
        }
        productRepository.saveAndFlush(product);
        return convertToProductResponse(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse deleteById(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product Not Found"));
        imageService.delete(product.getImage());
        productRepository.delete(product);
        return convertToProductResponse(product);
    }

    private ProductResponse convertToProductResponse(Product product){
        return ProductResponse.builder().id(product.getId()).name(product.getName())
                .price(product.getPrice()).sku(product.getSku()).quantity(product.getQuantity())
                .image(ImageResponse.builder().url("/api/products/image/"+product.getImage().getId())
                        .name(product.getImage().getName()).build()).build();
    }

    private Page<ProductResponse> convertToPageProductResponse(Page<Product> productResponses){
        return productResponses.map(this::convertToProductResponse);
    }
}
