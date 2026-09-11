
package com.petshop.app.controller;

import com.petshop.app.model.ResetToken;
import com.petshop.app.model.User;
import com.petshop.app.repositories.ResetTokenRepository;
import com.petshop.app.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final ResetTokenRepository resetTokenRepository;

    // Las sesiones todavía se mantienen en memoria.
    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    private static final Logger RESET_LOG =
            LoggerFactory.getLogger("resetTokenLogger");

    public AuthController(
            UserRepository userRepository,
            ResetTokenRepository resetTokenRepository) {

        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
    }

    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Faltan campos"));
        }

        // Buscar usuario directamente en SQL Server
        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user != null &&
                user.getPassword().equals(password)) {

            String token = UUID.randomUUID().toString();

            sessions.put(token, user);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            return ResponseEntity.ok(response);
        }

        return ResponseEntity
                .status(401)
                .body(Map.of("error", "Credenciales inválidas"));
    }

    // =========================
    // USUARIO ACTUAL
    // =========================

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @RequestHeader(
                    value = "X-Auth-Token",
                    required = false
            ) String token) {

        if (token != null && sessions.containsKey(token)) {
            return ResponseEntity.ok(
                    sessions.get(token)
            );
        }

        return ResponseEntity
                .status(401)
                .body(Map.of("error", "No autorizado"));
    }

    // =========================
    // REGISTRO
    // =========================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");
        String name = body.getOrDefault("name", "");

        if (email == null || password == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Faltan campos"));
        }

        // Verificar en SQL Server
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity
                    .status(409)
                    .body(Map.of("error", "Usuario ya existe"));
        }

        User user = new User(
                UUID.randomUUID().toString(),
                email,
                password,
                name
        );

        // Guardar en SQL Server
        userRepository.save(user);

        // Crear sesión
        String token = UUID.randomUUID().toString();

        sessions.put(token, user);

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "user", user
                )
        );
    }

    // =========================
    // RECUPERAR CONTRASEÑA
    // =========================

    @PostMapping("/recover")
    public ResponseEntity<?> recover(
            @RequestBody Map<String, String> body) {

        String email = body.get("email");

        if (email == null ||
                !userRepository.existsByEmail(email)) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Email no registrado"));
        }

        String token = UUID.randomUUID().toString();

        long expiry =
                System.currentTimeMillis()
                        + (15 * 60 * 1000);

        ResetToken resetToken =
                new ResetToken(
                        token,
                        email,
                        expiry
                );

        // Guardar token en SQL Server
        resetTokenRepository.save(resetToken);

        String msg =
                "[SIMULATED EMAIL] "
                        + Instant.now()
                        + " | Password reset token for "
                        + email
                        + ": "
                        + token
                        + " (expires in 15 minutes)";

        RESET_LOG.info(msg);

        return ResponseEntity.ok(
                Map.of(
                        "resetToken", token,
                        "expiresInMinutes", 15
                )
        );
    }

    // =========================
    // CAMBIAR CONTRASEÑA
    // =========================

    @PostMapping("/reset")
    public ResponseEntity<?> reset(
            @RequestBody Map<String, String> body) {

        String token = body.get("token");
        String newPassword = body.get("password");

        if (token == null || newPassword == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Faltan campos"));
        }

        // Buscar token en SQL Server
        ResetToken resetToken =
                resetTokenRepository
                        .findById(token)
                        .orElse(null);

        if (resetToken == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Token inválido"));
        }

        // Verificar expiración
        if (resetToken.isExpired()) {

            resetTokenRepository.deleteById(token);

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Token expirado"));
        }

        String email = resetToken.getEmail();

        // Buscar usuario en SQL Server
        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        // Actualizar contraseña
        user.setPassword(newPassword);

        userRepository.save(user);

        // El token solamente puede utilizarse una vez
        resetTokenRepository.deleteById(token);

        return ResponseEntity.ok(
                Map.of("ok", true)
        );
    }
}