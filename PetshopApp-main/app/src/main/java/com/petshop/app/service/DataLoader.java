package com.petshop.app.service;

import com.petshop.app.model.Category;
import com.petshop.app.model.Product;
import com.petshop.app.model.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import com.petshop.app.repositories.*;

@Component
public class DataLoader implements InitializingBean {

    private final InMemoryStore store;

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    public DataLoader(InMemoryStore store, CategoryRepository categoryRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.store = store;

        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        //  CATEGORIAS

        Category perros = new Category(
                "perros",
                "Perros",
                "#FFD6A5"
        );

        Category gatos = new Category(
                "gatos",
                "Gatos",
                "#BFEAF5"
        );

        Category alimentos = new Category(
                "alimentos",
                "Alimentos",
                "#FFE6CC"
        );

        Category juguetes = new Category(
                "juguetes",
                "Juguetes",
                "#FDE2E4"
        );

        store.categories.add(new Category("perros","Perros","#FFD6A5"));
        store.categories.add(new Category("gatos","Gatos","#BFEAF5"));
        store.categories.add(new Category("alimentos","Alimentos","#FFE6CC"));
        store.categories.add(new Category("juguetes","Juguetes","#FDE2E4"));

        store.products.add(new Product("p1","Croquetas SuperDog 10kg","SuperDog",12999.0,15999.0,4.6,"/images/p1.jpg","Oferta",perros,50));
        store.products.add(new Product("p2","Pelota Squeaky","PetPlay",499.0,null,4.3,"/images/p2.jpg",null,juguetes,200));
        store.products.add(new Product("p3","Collar Comfort L","WalkEasy",899.0,1099.0,4.5,"/images/p3.jpg","Más vendido",alimentos,120));
        store.products.add(new Product("p4","Arena para gato Premium 8L","GatoClean",649.0,799.0,4.4,"/images/p4.jpg","Oferta",gatos,80));

        // Demo user
        User u = new User("u1","cliente@ejemplo.com","password","Cliente Demo");
        store.users.put(u.getEmail(),u);
    }
}
