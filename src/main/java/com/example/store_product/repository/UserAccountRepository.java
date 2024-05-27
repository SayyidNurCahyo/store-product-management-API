package com.example.store_product.repository;

import com.example.store_product.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    UserAccount findByEmail(String email);
    UserAccount findByPhone(String phone);
}
