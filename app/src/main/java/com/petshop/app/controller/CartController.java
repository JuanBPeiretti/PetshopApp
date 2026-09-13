package com.petshop.app.controller;

import com.petshop.app.model.CartItem;
import com.petshop.app.model.Product;
import com.petshop.app.repository.ProductRepository;
import com.petshop.app.service.InMemoryStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final InMemoryStore store;
    private final ProductRepository productRepository;

    public CartController(InMemoryStore store, ProductRepository productRepository) {
        this.store = store;
        this.productRepository = productRepository;
    }

    private String resolveToken(String token) {
        return (token == null || token.isBlank()) ? "guest" : token;
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        List<CartItem> items = store.carts.getOrDefault(resolveToken(token), List.of());
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestHeader(value = "X-Auth-Token", required = false) String token, @RequestBody CartItem item) {
        String userToken = resolveToken(token);
        if (item == null || item.productId == null || item.productId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Producto inválido"));
        }

        Product product = productRepository.findById(item.productId).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Producto no encontrado"));
        }

        item.name = item.name == null || item.name.isBlank() ? product.name : item.name;
        item.variant = item.variant == null || item.variant.isBlank() ? product.categoryId : item.variant;
        item.price = product.price;
        item.quantity = Math.max(1, item.quantity);

        List<CartItem> cart = store.carts.computeIfAbsent(userToken, k -> new ArrayList<>());
        for (CartItem existing : cart) {
            if (existing.productId.equals(item.productId) && Objects.equals(existing.variant, item.variant)) {
                existing.quantity += item.quantity;
                return ResponseEntity.ok(cart);
            }
        }

        cart.add(item);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestHeader(value = "X-Auth-Token", required = false) String token, @RequestBody CartItem item) {
        String userToken = resolveToken(token);
        List<CartItem> list = store.carts.getOrDefault(userToken, new ArrayList<>());
        list.removeIf(i -> i.productId.equals(item.productId) && Objects.equals(i.variant, item.variant));
        store.carts.put(userToken, list);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        String userToken = resolveToken(token);
        List<CartItem> purchasedItems = store.carts.remove(userToken);
        if (purchasedItems == null) {
            purchasedItems = new ArrayList<>();
        }
        return ResponseEntity.ok(Map.of("ok", true, "items", purchasedItems));
    }
}
