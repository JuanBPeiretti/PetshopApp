package com.petshop.app.controller;

import com.petshop.app.repository.OrderRepository;
import com.petshop.app.service.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;

    public OrderController(OrderRepository orderRepository, JwtUtil jwtUtil) {
        this.orderRepository = orderRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public ResponseEntity<?> listMine(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }

        String userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(orderRepository.findByUserIdOrderByFechaDesc(userId));
    }
}
