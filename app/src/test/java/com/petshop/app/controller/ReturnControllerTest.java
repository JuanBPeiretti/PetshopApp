package com.petshop.app.controller;

import com.petshop.app.model.Product;
import com.petshop.app.model.ProductVariant;
import com.petshop.app.model.Return;
import com.petshop.app.repository.ProductRepository;
import com.petshop.app.repository.ProductVariantRepository;
import com.petshop.app.repository.ReturnRepository;
import com.petshop.app.service.AdminGuard;
import com.petshop.app.service.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReturnControllerTest {

    private static final String JWT_SECRET = "test-secret-test-secret-test-secret-test-secret";

    private ReturnRepository returnRepository;
    private ProductRepository productRepository;
    private ProductVariantRepository variantRepository;
    private JwtUtil jwtUtil;
    private AdminGuard adminGuard;
    private ReturnController controller;
    private String adminToken;

    @BeforeEach
    void setUp() {
        returnRepository = mock(ReturnRepository.class);
        productRepository = mock(ProductRepository.class);
        variantRepository = mock(ProductVariantRepository.class);
        jwtUtil = new JwtUtil(JWT_SECRET, 60_000);
        adminGuard = new AdminGuard(jwtUtil);
        controller = new ReturnController(returnRepository, productRepository, variantRepository, jwtUtil, adminGuard);
        adminToken = jwtUtil.generateToken("admin-1", "admin@example.com", "ADMIN");

        when(returnRepository.save(any(Return.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void approvingReturnRestocksProductAndVariant() {
        Product product = new Product("p1", "Collar", "Marca", 500.0, null, 4.5, "/img.jpg", null, "accesorios", 3);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        ProductVariant variant = new ProductVariant("p1", "M", "Negro", 1);
        variant.id = 10L;
        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));

        Return devolucion = new Return("user-1", "p1", 2, "No le gustó", Return.Status.PENDIENTE, Instant.now());
        devolucion.id = 1L;
        devolucion.variantId = 10L;
        when(returnRepository.findById(1L)).thenReturn(Optional.of(devolucion));

        ResponseEntity<?> response = controller.updateStatus(adminToken, 1L, Map.of("estado", "APROBADA"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(product.stock).isEqualTo(5);
        assertThat(variant.stock).isEqualTo(3);
    }

    @Test
    void rejectingReturnDoesNotRestockAnything() {
        Product product = new Product("p2", "Correa", "Marca", 300.0, null, 4.0, "/img.jpg", null, "accesorios", 5);
        when(productRepository.findById("p2")).thenReturn(Optional.of(product));

        Return devolucion = new Return("user-1", "p2", 2, "Producto dañado", Return.Status.PENDIENTE, Instant.now());
        devolucion.id = 2L;
        when(returnRepository.findById(2L)).thenReturn(Optional.of(devolucion));

        ResponseEntity<?> response = controller.updateStatus(adminToken, 2L, Map.of("estado", "RECHAZADA"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(product.stock).isEqualTo(5);
    }

    @Test
    void cannotResolveAReturnTwice() {
        Product product = new Product("p3", "Cama", "Marca", 1000.0, null, 4.0, "/img.jpg", null, "accesorios", 1);
        when(productRepository.findById("p3")).thenReturn(Optional.of(product));

        Return devolucion = new Return("user-1", "p3", 1, "Motivo", Return.Status.APROBADA, Instant.now());
        devolucion.id = 3L;
        when(returnRepository.findById(3L)).thenReturn(Optional.of(devolucion));

        ResponseEntity<?> response = controller.updateStatus(adminToken, 3L, Map.of("estado", "APROBADA"));

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(product.stock).isEqualTo(1);
    }

    @Test
    void nonAdminCannotResolveReturns() {
        String customerToken = jwtUtil.generateToken("user-1", "cliente@example.com", "CUSTOMER");

        ResponseEntity<?> response = controller.updateStatus(customerToken, 1L, Map.of("estado", "APROBADA"));

        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }
}
