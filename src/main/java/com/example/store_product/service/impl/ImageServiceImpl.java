package com.example.store_product.service.impl;

import com.example.store_product.entity.Image;
import com.example.store_product.entity.Product;
import com.example.store_product.repository.ImageRepository;
import com.example.store_product.service.ImageService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final Path directoryPath;
    @Autowired
    public ImageServiceImpl(@Value("${store.multipart.path-location}") String directoryPath, ImageRepository imageRepository){
        this.directoryPath = Paths.get(directoryPath);
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    public void initDirectory(){
        if (!Files.exists(directoryPath)){
            try {
                Files.createDirectory(directoryPath);
            }catch (IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Image addImage(MultipartFile image) {
        try {
            if(!List.of("image/jpeg", "image/jpg", "image/png").contains(image.getContentType()))
                throw new ConstraintViolationException("File Must Be in Image Format (jpg, jpeg, png)",null);
            String imageName = System.currentTimeMillis()+"_"+image.getOriginalFilename();
            Path imagePath = directoryPath.resolve(imageName);
            Files.copy(image.getInputStream(),imagePath);
            Image imageSave = Image.builder().name(imageName).path(imagePath.toString())
                    .size(image.getSize()).contentType(image.getContentType()).build();
            return imageRepository.saveAndFlush(imageSave);
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Resource getById(String id) {
        try {
            Image image = imageRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image Not Found"));
            Path filePath = Paths.get(image.getPath());
            if (!Files.exists(filePath)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image Not Found");
            return new UrlResource(filePath.toUri());
        } catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Image image) {
        try {
            imageRepository.findById(image.getId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image Not Found"));
            Path filePath = Paths.get(image.getPath());
            if (!Files.exists(filePath)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image Not Found");
            Files.delete(filePath);
            imageRepository.delete(image);
        } catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
