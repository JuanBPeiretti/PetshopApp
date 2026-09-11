package com.petshop.app.controller;

import com.petshop.app.model.CartItem;
import com.petshop.app.model.Product;
import com.petshop.app.repositories.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final ProductRepository productRepository;

    // Por ahora el carrito sigue en memoria.
    // Los productos y sus precios vienen desde SQL Server.
    private final ConcurrentHashMap<String, List<CartItem>> carts =
            new ConcurrentHashMap<>();

    public CartController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<?> getCart(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {

        if (token == null) {
            token = "guest";
        }

        List<CartItem> items = carts.getOrDefault(token, new ArrayList<>());

        return ResponseEntity.ok(items);
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @RequestBody CartItem item) {

        if (token == null) {
            token = "guest";
        }

        // Buscar el producto directamente en SQL Server
        Product product = productRepository
                .findById(item.productId)
                .orElse(null);

        if (product == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Producto no encontrado"));
        }

        // Obtener el precio real desde la base de datos
        item.price = product.getPrice();

        // Crear carrito si todavía no existe
        carts.computeIfAbsent(
                token,
                k -> new ArrayList<>()
        );

        carts.get(token).add(item);

        return ResponseEntity.ok(carts.get(token));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @RequestBody CartItem item) {

        if (token == null) {
            token = "guest";
        }

        List<CartItem> list = carts.getOrDefault(
                token,
                new ArrayList<>()
        );

        list.removeIf(i ->
                i.productId != null &&
                        i.productId.equals(item.productId)
        );

        carts.put(token, list);

        return ResponseEntity.ok(list);
    }
}
