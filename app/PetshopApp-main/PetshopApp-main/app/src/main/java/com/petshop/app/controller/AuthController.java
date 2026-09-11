package com.petshop.app.controller;

import com.petshop.app.model.User;
import com.petshop.app.service.InMemoryStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final InMemoryStore store;
    private static final Logger RESET_LOG = LoggerFactory.getLogger("resetTokenLogger");

    public AuthController(InMemoryStore store) {
        this.store = store;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User u = store.users.get(email);
        if (u != null && u.password.equals(password)) {
            String token = UUID.randomUUID().toString();
            store.sessions.put(token, u);
            Map<String,Object> resp = new HashMap<>();
            resp.put("token",token);
            resp.put("user",u);
            return ResponseEntity.ok(resp);
        }

        return ResponseEntity.status(401).body(Map.of("error","Credenciales inválidas"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (token != null && store.sessions.containsKey(token)) {
            return ResponseEntity.ok(store.sessions.get(token));
        }
        return ResponseEntity.status(401).body(Map.of("error","No autorizado"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String name = body.getOrDefault("name", "");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Faltan campos"));
        }

        if (store.users.containsKey(email)) {
            return ResponseEntity.status(409).body(Map.of("error","Usuario ya existe"));
        }

        User u = new User(UUID.randomUUID().toString(), email, password, name);
        store.users.put(email, u);
        String token = UUID.randomUUID().toString();
        store.sessions.put(token, u);
        return ResponseEntity.ok(Map.of("token", token, "user", u));
    }

    @PostMapping("/recover")
    public ResponseEntity<?> recover(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        if (email == null || !store.users.containsKey(email)) {
            return ResponseEntity.badRequest().body(Map.of("error","Email no registrado"));
        }

        String token = UUID.randomUUID().toString();
        long expiry = System.currentTimeMillis() + (15 * 60 * 1000); // 15 minutes
        com.petshop.app.model.ResetToken rt = new com.petshop.app.model.ResetToken(token, email, expiry);
        store.resetTokens.put(token, rt);

        // Log via SLF4J/Logback so it can be managed/rotated centrally.
        String msg = "[SIMULATED EMAIL] " + Instant.now().toString() + " | Password reset token for " + email + ": " + token + " (expires in 15 minutes)";
        RESET_LOG.info(msg);

        return ResponseEntity.ok(Map.of("resetToken", token, "expiresInMinutes", 15));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody Map<String,String> body) {
        String token = body.get("token");
        String newPassword = body.get("password");
        if (token == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Faltan campos"));
        }

        com.petshop.app.model.ResetToken rt = store.resetTokens.get(token);
        if (rt == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Token inválido"));
        }

        if (rt.isExpired()) {
            store.resetTokens.remove(token);
            return ResponseEntity.badRequest().body(Map.of("error","Token expirado"));
        }

        String email = rt.email;
        User u = store.users.get(email);
        if (u == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Usuario no encontrado"));
        }

        u.password = newPassword;
        store.resetTokens.remove(token);
        return ResponseEntity.ok(Map.of("ok",true));
    }
}
