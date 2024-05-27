package com.example.store_product.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "m_image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "path", nullable = false)
    private String path;
    @Column(name = "size", nullable = false, columnDefinition = "BIGINT CHECK (size>=0)")
    private Long size;
    @Column(name = "content_type", nullable = false)
    private String contentType;
}
