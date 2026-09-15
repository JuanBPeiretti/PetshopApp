package com.petshop.app.controller;

import com.petshop.app.model.Product;
import com.petshop.app.repository.ProductRepository;
import com.petshop.app.service.PriceAscStrategy;
import com.petshop.app.service.PriceDescStrategy;
import com.petshop.app.service.ProductSortStrategy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductSortStrategy priceAscStrategy;
    private final ProductSortStrategy priceDescStrategy;

    public ProductController(ProductRepository productRepository, PriceAscStrategy priceAscStrategy, PriceDescStrategy priceDescStrategy) {
        this.productRepository = productRepository;
        this.priceAscStrategy = priceAscStrategy;
        this.priceDescStrategy = priceDescStrategy;
    }

    @GetMapping
    public List<Product> list(@RequestParam(required = false) String category, @RequestParam(required = false) String sort) {
        List<Product> filtered = (category == null || category.isBlank())
                ? productRepository.findAll()
                : productRepository.findByCategoryIdIgnoreCase(category);

        ProductSortStrategy strategy = resolveStrategy(sort);
        if (strategy != null) {
            filtered = strategy.sort(filtered);
        }

        return filtered;
    }

    private ProductSortStrategy resolveStrategy(String sort) {
        if ("Menor precio".equalsIgnoreCase(sort) || "menorprecio".equalsIgnoreCase(sort)) {
            return priceAscStrategy;
        }
        if ("Mayor precio".equalsIgnoreCase(sort) || "mayorprecio".equalsIgnoreCase(sort)) {
            return priceDescStrategy;
        }
        return null;
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable String id) {
        return productRepository.findById(id).orElse(null);
    }
}
