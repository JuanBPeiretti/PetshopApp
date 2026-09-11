package com.petshop.app.controller;

import com.petshop.app.model.CartItem;
import com.petshop.app.model.Product;
import com.petshop.app.service.InMemoryStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final InMemoryStore store;

    public CartController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        List<CartItem> items = store.carts.getOrDefault(token, List.of());
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestHeader(value = "X-Auth-Token", required = false) String token, @RequestBody CartItem item) {
        if (token == null) token = "guest";
        store.carts.computeIfAbsent(token, k -> new java.util.ArrayList<>());
        // try to find product and set price
        Product p = store.findProduct(item.productId).orElse(null);
        if (p == null) return ResponseEntity.badRequest().body(java.util.Map.of("error","Producto no encontrado"));
        item.price = p.price;
        store.carts.get(token).add(item);
        return ResponseEntity.ok(store.carts.get(token));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestHeader(value = "X-Auth-Token", required = false) String token, @RequestBody CartItem item) {
        if (token == null) token = "guest";
        List<CartItem> list = store.carts.getOrDefault(token, new java.util.ArrayList<>());
        list.removeIf(i -> i.productId.equals(item.productId));
        store.carts.put(token,list);
        return ResponseEntity.ok(list);
    }
}
