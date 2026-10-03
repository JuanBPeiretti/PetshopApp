package com.petshop.app.controller;

import com.petshop.app.model.User;
import com.petshop.app.repository.ResetTokenRepository;
import com.petshop.app.repository.UserRepository;
import com.petshop.app.service.JwtUtil;
import com.petshop.app.service.LoginRateLimiter;
import com.petshop.app.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private static final String JWT_SECRET = "test-secret-test-secret-test-secret-test-secret";
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private UserRepository userRepository;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        JwtUtil jwtUtil = new JwtUtil(JWT_SECRET, 60_000);
        ResetTokenRepository resetTokenRepository = mock(ResetTokenRepository.class);
        NotificationService notificationService = mock(NotificationService.class);
        LoginRateLimiter loginRateLimiter = new LoginRateLimiter();
        controller = new AuthController(jwtUtil, userRepository, resetTokenRepository, notificationService, loginRateLimiter);
    }

    @Test
    void disabledAccountCannotLoginEvenWithCorrectPassword() {
        User user = new User("user-1", "cliente@example.com", ENCODER.encode("password123"), "Cliente");
        user.active = false;
        when(userRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.login(Map.of("email", "cliente@example.com", "password", "password123"));

        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }

    @Test
    void activeAccountCanLoginWithCorrectPassword() {
        User user = new User("user-1", "cliente@example.com", ENCODER.encode("password123"), "Cliente");
        when(userRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.login(Map.of("email", "cliente@example.com", "password", "password123"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
