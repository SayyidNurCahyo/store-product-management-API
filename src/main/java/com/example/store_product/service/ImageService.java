package com.example.store_product.service;

import com.example.store_product.entity.Image;
import com.example.store_product.entity.Product;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    Image addImage(MultipartFile image);
    Resource getById(String id);
    void delete(Image image);
}
