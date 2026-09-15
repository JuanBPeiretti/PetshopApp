package com.petshop.app;

import com.petshop.app.controller.CartController;
import com.petshop.app.model.CartItem;
import com.petshop.app.model.Product;
import com.petshop.app.repository.ProductRepository;
import com.petshop.app.service.InMemoryStore;
import com.petshop.app.service.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppApplicationTests {

    private static final String JWT_SECRET = "test-secret-test-secret-test-secret-test-secret";

    private InMemoryStore store;
    private ProductRepository productRepository;
    private JwtUtil jwtUtil;
    private CartController cartController;

    @BeforeEach
    void setUp() {
        store = new InMemoryStore();
        productRepository = mock(ProductRepository.class);
        jwtUtil = new JwtUtil(JWT_SECRET, 60_000);
        cartController = new CartController(store, productRepository, jwtUtil);
    }

    @Test
    void cartCanAddAndCheckoutProducts() {
        Product product = new Product(
            "p-cart-1",
            "Producto carrito",
            "Marca carrito",
            950.0,
            null,
            4.8,
            "/images/cart-test.jpg",
            "Nuevo",
            "alimentos",
            15
        );
        when(productRepository.findById("p-cart-1")).thenReturn(Optional.of(product));

        String token = jwtUtil.generateToken("user-1", "user1@example.com");

        ResponseEntity<?> added = cartController.add(token, new CartItem("p-cart-1", "Producto carrito", "alimentos", 1, 950.0));
        assertThat(added.getStatusCode().is2xxSuccessful()).isTrue();

        List<CartItem> items = (List<CartItem>) added.getBody();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).productId).isEqualTo("p-cart-1");
        assertThat(items.get(0).quantity).isEqualTo(1);
        assertThat(store.carts).containsKey("user-1");

        ResponseEntity<?> checkout = cartController.checkout(token);
        assertThat(checkout.getStatusCode().is2xxSuccessful()).isTrue();

        Map<?, ?> body = (Map<?, ?>) checkout.getBody();
        assertThat(body.get("ok")).isEqualTo(true);
        assertThat(store.carts).doesNotContainKey("user-1");
    }

    @Test
    void cartFallsBackToGuestBucketForMissingOrInvalidToken() {
        Product product = new Product(
            "p-cart-2",
            "Producto invitado",
            "Marca carrito",
            500.0,
            null,
            4.2,
            "/images/cart-test-2.jpg",
            "Nuevo",
            "alimentos",
            10
        );
        when(productRepository.findById("p-cart-2")).thenReturn(Optional.of(product));

        cartController.add(null, new CartItem("p-cart-2", "Producto invitado", "alimentos", 1, 500.0));
        cartController.add("not-a-real-jwt", new CartItem("p-cart-2", "Producto invitado", "alimentos", 1, 500.0));

        assertThat(store.carts).containsKey("guest");
        assertThat(store.carts.get("guest")).hasSize(1);
        assertThat(store.carts.get("guest").get(0).quantity).isEqualTo(2);
    }
}

