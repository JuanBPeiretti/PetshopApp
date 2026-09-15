package com.petshop.app.controller;

import com.petshop.app.model.Review;
import com.petshop.app.repository.ProductRepository;
import com.petshop.app.repository.ReviewRepository;
import com.petshop.app.service.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;

    public ReviewController(ReviewRepository reviewRepository, ProductRepository productRepository, JwtUtil jwtUtil) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<?> list(@PathVariable String productId) {
        return ResponseEntity.ok(reviewRepository.findByProductIdOrderByCreatedAtDesc(productId));
    }

    @PostMapping
    public ResponseEntity<?> add(@PathVariable String productId,
                                  @RequestHeader(value = "X-Auth-Token", required = false) String token,
                                  @RequestBody Map<String, Object> body) {
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }

        if (!productRepository.existsById(productId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Producto no encontrado"));
        }

        Object ratingValue = body.get("rating");
        if (!(ratingValue instanceof Number)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Rating inválido"));
        }
        int rating = ((Number) ratingValue).intValue();
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(Map.of("error", "El rating debe ser entre 1 y 5"));
        }

        String comment = body.get("comment") != null ? body.get("comment").toString() : "";
        String userId = jwtUtil.extractUserId(token);

        Review review = new Review(productId, userId, rating, comment, Instant.now());
        reviewRepository.save(review);
        return ResponseEntity.ok(review);
    }
}
