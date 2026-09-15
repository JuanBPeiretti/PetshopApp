package com.petshop.app.controller;

import com.petshop.app.model.CartItem;
import com.petshop.app.model.Product;
import com.petshop.app.repository.CartItemRepository;
import com.petshop.app.repository.ProductRepository;
import com.petshop.app.service.InMemoryStore;
import com.petshop.app.service.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final String GUEST = "guest";

    private final InMemoryStore store;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final JwtUtil jwtUtil;

    public CartController(InMemoryStore store, ProductRepository productRepository, CartItemRepository cartItemRepository, JwtUtil jwtUtil) {
        this.store = store;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.jwtUtil = jwtUtil;
    }

    private String resolveToken(String token) {
        if (token == null || token.isBlank() || !jwtUtil.isTokenValid(token)) {
            return GUEST;
        }
        return jwtUtil.extractUserId(token);
    }

    private boolean isGuest(String userToken) {
        return GUEST.equals(userToken);
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        String userToken = resolveToken(token);
        List<CartItem> items = isGuest(userToken)
                ? store.carts.getOrDefault(userToken, List.of())
                : cartItemRepository.findByUserId(userToken);
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

        if (isGuest(userToken)) {
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

        List<CartItem> cart = cartItemRepository.findByUserId(userToken);
        for (CartItem existing : cart) {
            if (existing.productId.equals(item.productId) && Objects.equals(existing.variant, item.variant)) {
                existing.quantity += item.quantity;
                cartItemRepository.save(existing);
                return ResponseEntity.ok(cartItemRepository.findByUserId(userToken));
            }
        }

        item.userId = userToken;
        cartItemRepository.save(item);
        return ResponseEntity.ok(cartItemRepository.findByUserId(userToken));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestHeader(value = "X-Auth-Token", required = false) String token, @RequestBody CartItem item) {
        String userToken = resolveToken(token);

        if (isGuest(userToken)) {
            List<CartItem> list = store.carts.getOrDefault(userToken, new ArrayList<>());
            list.removeIf(i -> i.productId.equals(item.productId) && Objects.equals(i.variant, item.variant));
            store.carts.put(userToken, list);
            return ResponseEntity.ok(list);
        }

        List<CartItem> cart = cartItemRepository.findByUserId(userToken);
        List<CartItem> toDelete = cart.stream()
                .filter(i -> i.productId.equals(item.productId) && Objects.equals(i.variant, item.variant))
                .toList();
        cartItemRepository.deleteAll(toDelete);
        return ResponseEntity.ok(cartItemRepository.findByUserId(userToken));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        String userToken = resolveToken(token);

        List<CartItem> purchasedItems;
        if (isGuest(userToken)) {
            purchasedItems = store.carts.remove(userToken);
            if (purchasedItems == null) {
                purchasedItems = new ArrayList<>();
            }
        } else {
            purchasedItems = cartItemRepository.findByUserId(userToken);
            cartItemRepository.deleteAll(purchasedItems);
        }
        return ResponseEntity.ok(Map.of("ok", true, "items", purchasedItems));
    }
}
