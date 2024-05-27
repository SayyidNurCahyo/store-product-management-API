package com.example.store_product.controller;

import com.example.store_product.dto.request.NewProductRequest;
import com.example.store_product.dto.request.SearchProductRequest;
import com.example.store_product.dto.request.UpdateProductRequest;
import com.example.store_product.dto.response.CommonResponse;
import com.example.store_product.dto.response.PagingResponse;
import com.example.store_product.dto.response.ProductResponse;
import com.example.store_product.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/products")
public class ProductController {
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<ProductResponse>> addProduct(@RequestPart(name = "product") String jsonProduct,
                                                                      @RequestPart(name = "image") MultipartFile image) {
        CommonResponse<ProductResponse> response;
        try {
            NewProductRequest request = objectMapper.readValue(jsonProduct, new TypeReference<NewProductRequest>() {});
            request.setImage(image);
            ProductResponse productAdded = productService.addProduct(request);
            response = CommonResponse.<ProductResponse>builder()
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Successfully Save Data")
                    .data(productAdded).build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (IOException e){
            response = CommonResponse.<ProductResponse>builder().message("Internal Server Error")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<ProductResponse>> getProductById(@PathVariable String id) {
        ProductResponse product = productService.getProductById(id);
        CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully Get Data")
                .data(product).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ProductResponse>>> getAllProduct(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "name", required = false) String name
    ) {
        SearchProductRequest request = SearchProductRequest.builder()
                .page(page).size(size).sortBy(sortBy).direction(direction)
                .name(name).build();
        Page<ProductResponse> products = productService.getAllProduct(request);
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(products.getTotalPages())
                .totalElement(products.getTotalElements())
                .page(products.getPageable().getPageNumber()+1)
                .size(products.getPageable().getPageSize())
                .hasNext(products.hasNext())
                .hasPrevious(products.hasPrevious()).build();
        CommonResponse<List<ProductResponse>> response = CommonResponse.<List<ProductResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully Get Data")
                .data(products.getContent())
                .paging(pagingResponse).build();
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping
    public ResponseEntity<CommonResponse<ProductResponse>> updateProduct(@RequestPart(name = "product") String jsonProduct,
                                                                   @RequestPart(name = "image", required = false) MultipartFile image) {
        CommonResponse<ProductResponse> response;
        try {
            UpdateProductRequest request = objectMapper.readValue(jsonProduct, new TypeReference<UpdateProductRequest>() {});
            if (image!=null) request.setImage(image);
            ProductResponse product = productService.updateProduct(request);
            response = CommonResponse.<ProductResponse>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Successfully Update Data")
                    .data(product).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (IOException e){
            response = CommonResponse.<ProductResponse>builder().message("Internal Server Error")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<ProductResponse>> deleteById(@PathVariable String id) {
        ProductResponse product = productService.deleteById(id);
        CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfuly Delete Data")
                .data(product).build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
