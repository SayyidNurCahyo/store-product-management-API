package com.example.store_product.controller;

import com.example.store_product.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping(path = "/api/products/image" + "/{imageId}")
    public ResponseEntity<Resource> download(@PathVariable(name = "imageId") String id){
        Resource resource = imageService.getById(id);
        String headerValue = String.format("attachment; filename=%s", resource.getFilename());
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(resource);
    }
}
