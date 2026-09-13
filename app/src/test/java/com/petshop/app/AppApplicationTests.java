package com.petshop.app;

import com.petshop.app.controller.CartController;
import com.petshop.app.model.CartItem;
import com.petshop.app.model.Product;
import com.petshop.app.repository.ProductRepository;
import com.petshop.app.service.InMemoryStore;
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

    private InMemoryStore store;
    private ProductRepository productRepository;
    private CartController cartController;

    @BeforeEach
    void setUp() {
        store = new InMemoryStore();
        productRepository = mock(ProductRepository.class);
        cartController = new CartController(store, productRepository);
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

        ResponseEntity<?> added = cartController.add("token-1", new CartItem("p-cart-1", "Producto carrito", "alimentos", 1, 950.0));
        assertThat(added.getStatusCode().is2xxSuccessful()).isTrue();

        List<CartItem> items = (List<CartItem>) added.getBody();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).productId).isEqualTo("p-cart-1");
        assertThat(items.get(0).quantity).isEqualTo(1);

        ResponseEntity<?> checkout = cartController.checkout("token-1");
        assertThat(checkout.getStatusCode().is2xxSuccessful()).isTrue();

        Map<?, ?> body = (Map<?, ?>) checkout.getBody();
        assertThat(body.get("ok")).isEqualTo(true);
        assertThat(store.carts).doesNotContainKey("token-1");
    }
}

