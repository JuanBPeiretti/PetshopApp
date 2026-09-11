
package com.petshop.app.controller;

import com.petshop.app.model.Product;
import com.petshop.app.repositories.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort) {

        // Obtener productos desde SQL Server
        List<Product> filtered = productRepository.findAll()
                .stream()
                .filter(p ->
                        category == null ||
                                (p.getCategory() != null &&
                                        p.getCategory().getId().equalsIgnoreCase(category))
                )
                .collect(Collectors.toList());

        // Ordenar por precio
        if ("Menor precio".equalsIgnoreCase(sort)
                || "menorprecio".equalsIgnoreCase(sort)) {

            filtered.sort(
                    Comparator.comparingDouble(Product::getPrice)
            );

        } else if ("Mayor precio".equalsIgnoreCase(sort)
                || "mayorprecio".equalsIgnoreCase(sort)) {

            filtered.sort(
                    Comparator.comparingDouble(Product::getPrice)
                            .reversed()
            );
        }

        return filtered;
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable String id) {

        return productRepository
                .findById(id)
                .orElse(null);
    }
}
