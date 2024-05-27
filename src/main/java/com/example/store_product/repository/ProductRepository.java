package com.example.store_product.repository;

import com.example.store_product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query(value = "select * from m_product where name ilike name", nativeQuery = true)
    Page<Product> findProduct(@Param("name") String name, Pageable pageable);
}
