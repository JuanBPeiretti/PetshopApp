package com.petshop.app.repository;

import com.petshop.app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategoryIdIgnoreCase(String categoryId);
}
