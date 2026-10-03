package com.petshop.app.controller;

import com.petshop.app.model.User;
import com.petshop.app.repository.UserRepository;
import com.petshop.app.service.AdminGuard;
import com.petshop.app.service.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private static final String JWT_SECRET = "test-secret-test-secret-test-secret-test-secret";

    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private AdminGuard adminGuard;
    private UserController controller;
    private String adminToken;
    private String adminId;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtUtil = new JwtUtil(JWT_SECRET, 60_000);
        adminGuard = new AdminGuard(jwtUtil);
        controller = new UserController(userRepository, jwtUtil, adminGuard);
        adminId = "admin-1";
        adminToken = jwtUtil.generateToken(adminId, "admin@example.com", "ADMIN");
    }

    @Test
    void adminCanPromoteACustomerToAdmin() {
        User customer = new User("user-1", "cliente@example.com", "hash", "Cliente");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(customer));

        ResponseEntity<?> response = controller.updateRole(adminToken, "user-1", Map.of("role", "ADMIN"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(customer.role).isEqualTo("ADMIN");
    }

    @Test
    void adminCanDisableACustomerAccount() {
        User customer = new User("user-1", "cliente@example.com", "hash", "Cliente");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(customer));

        ResponseEntity<?> response = controller.updateStatus(adminToken, "user-1", Map.of("active", false));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(customer.active).isFalse();
    }

    @Test
    void adminCannotChangeTheirOwnRole() {
        ResponseEntity<?> response = controller.updateRole(adminToken, adminId, Map.of("role", "CUSTOMER"));

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void adminCannotDisableTheirOwnAccount() {
        ResponseEntity<?> response = controller.updateStatus(adminToken, adminId, Map.of("active", false));

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void nonAdminCannotListOrChangeUsers() {
        String customerToken = jwtUtil.generateToken("user-1", "cliente@example.com", "CUSTOMER");

        assertThat(controller.list(customerToken).getStatusCode().value()).isEqualTo(403);
        assertThat(controller.updateRole(customerToken, "user-2", Map.of("role", "ADMIN")).getStatusCode().value()).isEqualTo(403);
        assertThat(controller.updateStatus(customerToken, "user-2", Map.of("active", false)).getStatusCode().value()).isEqualTo(403);
    }

    @Test
    void rejectsInvalidRoleValue() {
        ResponseEntity<?> response = controller.updateRole(adminToken, "user-1", Map.of("role", "SUPERUSER"));

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }
}
