package com.petshop.app.repositories;

import com.petshop.app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByCategoryId(String categoryId);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByBrandIgnoreCase(String brand);
}