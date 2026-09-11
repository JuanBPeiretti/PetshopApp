package com.petshop.app.controller;

import com.petshop.app.model.Product;
import com.petshop.app.service.InMemoryStore;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final InMemoryStore store;

    public ProductController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping
    public List<Product> list(@RequestParam(required = false) String category, @RequestParam(required = false) String sort) {
        List<Product> filtered = store.products.stream()
                .filter(p -> category == null || p.categoryId.equalsIgnoreCase(category))
                .collect(Collectors.toList());

        if ("Menor precio".equalsIgnoreCase(sort) || "menorprecio".equalsIgnoreCase(sort)) {
            filtered.sort(Comparator.comparingDouble(p -> p.price));
        } else if ("Mayor precio".equalsIgnoreCase(sort) || "mayorprecio".equalsIgnoreCase(sort)) {
            filtered.sort(Comparator.comparingDouble((Product p) -> p.price).reversed());
        }

        return filtered;
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable String id) {
        return store.findProduct(id).orElse(null);
    }
}
