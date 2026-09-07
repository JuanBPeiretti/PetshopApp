package com.petshop.app.controller;

import com.petshop.app.model.Category;
import com.petshop.app.service.InMemoryStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final InMemoryStore store;

    public CategoryController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping
    public List<Category> list() {
        return store.categories;
    }
}
