package com.petshop.app.controller;

import com.petshop.app.model.Product;
import com.petshop.app.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> list(@RequestParam(required = false) String category, @RequestParam(required = false) String sort) {
        List<Product> filtered = (category == null || category.isBlank())
                ? productRepository.findAll()
                : productRepository.findByCategoryIdIgnoreCase(category);

        if ("Menor precio".equalsIgnoreCase(sort) || "menorprecio".equalsIgnoreCase(sort)) {
            filtered.sort(Comparator.comparingDouble(p -> p.price));
        } else if ("Mayor precio".equalsIgnoreCase(sort) || "mayorprecio".equalsIgnoreCase(sort)) {
            filtered.sort(Comparator.comparingDouble((Product p) -> p.price).reversed());
        }

        return filtered;
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable String id) {
        return productRepository.findById(id).orElse(null);
    }
}
