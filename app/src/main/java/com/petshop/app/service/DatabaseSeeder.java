package com.petshop.app.service;

import com.petshop.app.model.Category;
import com.petshop.app.model.Product;
import com.petshop.app.model.User;
import com.petshop.app.repository.CategoryRepository;
import com.petshop.app.repository.ProductRepository;
import com.petshop.app.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DatabaseSeeder(CategoryRepository categoryRepository,
                          ProductRepository productRepository,
                          UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category("perros", "Perros", "#FFD6A5"));
            categoryRepository.save(new Category("gatos", "Gatos", "#BFEAF5"));
            categoryRepository.save(new Category("alimentos", "Alimentos", "#FFE6CC"));
            categoryRepository.save(new Category("juguetes", "Juguetes", "#FDE2E4"));
            categoryRepository.save(new Category("accesorios", "Accesorios", "#E9D5FF"));
            categoryRepository.save(new Category("higiene", "Higiene", "#D1FAE5"));
            categoryRepository.save(new Category("salud", "Salud", "#DBEAFE"));
        }

        if (productRepository.count() == 0) {
            productRepository.save(new Product("p1", "Croquetas SuperDog 10kg", "SuperDog", 12999.0, 15999.0, 4.6, "/images/p1.jpg", "Oferta", "alimentos", 50));
            productRepository.save(new Product("p2", "Pelota Squeaky", "PetPlay", 499.0, null, 4.3, "/images/p2.jpg", null, "juguetes", 200));
            productRepository.save(new Product("p3", "Collar Comfort L", "WalkEasy", 899.0, 1099.0, 4.5, "/images/p3.jpg", "Más vendido", "accesorios", 120));
            productRepository.save(new Product("p4", "Arena para gato Premium 8L", "GatoClean", 649.0, 799.0, 4.4, "/images/p4.jpg", "Oferta", "alimentos", 80));
            productRepository.save(new Product("p5", "Snack Dental Mini", "BiteCare", 699.0, 899.0, 4.7, "/images/p5.jpg", "Nuevo", "salud", 140));
            productRepository.save(new Product("p6", "Cepillo para perros", "PetClean", 850.0, 1100.0, 4.4, "/images/p6.jpg", "Oferta", "higiene", 90));
            productRepository.save(new Product("p7", "Rascador para gatos", "CatJoy", 2499.0, null, 4.8, "/images/p7.jpg", "Top", "juguetes", 60));
            productRepository.save(new Product("p8", "Juguete masticable", "ChewFun", 1199.0, 1499.0, 4.6, "/images/p8.jpg", "Más vendido", "juguetes", 180));
            productRepository.save(new Product("p9", "Pasta dental para perros", "DogSmile", 999.0, null, 4.5, "/images/p9.jpg", null, "salud", 110));
            productRepository.save(new Product("p10", "Cama para perro mediana", "CozyNest", 4599.0, 5200.0, 4.7, "/images/p10.jpg", "Oferta", "accesorios", 35));
            productRepository.save(new Product("p11", "Comida húmeda gatitos 3kg", "CatBites", 3299.0, 3899.0, 4.6, "/images/p11.jpg", "Nuevo", "alimentos", 75));
            productRepository.save(new Product("p12", "Peluche de lana para perro", "PawJoy", 799.0, 1099.0, 4.3, "/images/p12.jpg", null, "juguetes", 170));
            productRepository.save(new Product("p13", "Arnés de paseo pequeño", "TrailFit", 1599.0, 1899.0, 4.5, "/images/p13.jpg", "Popular", "accesorios", 130));
            productRepository.save(new Product("p14", "Shampoo antipulgas", "FreshPaw", 1299.0, 1499.0, 4.7, "/images/p14.jpg", "Top", "higiene", 88));
            productRepository.save(new Product("p15", "Vitaminas para perros", "VitalPet", 1799.0, null, 4.8, "/images/p15.jpg", "Recomendado", "salud", 95));
            productRepository.save(new Product("p16", "Bowl doble antiresbalón", "FeederMax", 1099.0, 1399.0, 4.4, "/images/p16.jpg", "Oferta", "accesorios", 160));
            productRepository.save(new Product("p17", "Cucha para gatos pequeña", "CatNest", 3599.0, 4199.0, 4.7, "/images/p17.jpg", "Oferta", "accesorios", 50));
            productRepository.save(new Product("p18", "Hueso masticable XXL", "ChewPro", 1499.0, 1899.0, 4.6, "/images/p18.jpg", "Nuevo", "juguetes", 110));
            productRepository.save(new Product("p19", "Toallitas húmedas para mascotas", "CleanPaw", 899.0, null, 4.2, "/images/p19.jpg", null, "higiene", 190));
            productRepository.save(new Product("p20", "Suplemento articular", "JointCare", 2099.0, 2499.0, 4.9, "/images/p20.jpg", "Top", "salud", 78));
            productRepository.save(new Product("p21", "Comedero automático", "PetAuto", 4999.0, 5899.0, 4.8, "/images/p21.jpg", "Nuevo", "accesorios", 42));
            productRepository.save(new Product("p22", "Tirador para gatos", "KittieFun", 699.0, 899.0, 4.5, "/images/p22.jpg", "Eco", "juguetes", 150));
            productRepository.save(new Product("p23", "Snacks naturales pollo", "BarkBites", 1099.0, null, 4.6, "/images/p23.jpg", "Nuevo", "alimentos", 140));
            productRepository.save(new Product("p24", "Limpieza de oído perros", "EarSafe", 1499.0, 1899.0, 4.7, "/images/p24.jpg", "Recomendado", "salud", 86));
            productRepository.save(new Product("p25", "Guante para cepillar", "SoftBrush", 559.0, 799.0, 4.3, "/images/p25.jpg", "Oferta", "higiene", 175));
            productRepository.save(new Product("p26", "Lazo de seguridad", "SafeWalk", 899.0, 1199.0, 4.4, "/images/p26.jpg", null, "accesorios", 120));
            productRepository.save(new Product("p27", "Pañales para cachorros", "PuppyCare", 1199.0, 1499.0, 4.5, "/images/p27.jpg", "Oferta", "higiene", 100));
            productRepository.save(new Product("p28", "Mouse para gatos", "CatMouse", 499.0, null, 4.6, "/images/p28.jpg", "Popular", "juguetes", 210));
            productRepository.save(new Product("p29", "Pasta dental gatos", "FelineSmile", 1099.0, null, 4.5, "/images/p29.jpg", null, "salud", 96));
            productRepository.save(new Product("p30", "Pañuelo absorbente", "PetWipe", 799.0, 999.0, 4.2, "/images/p30.jpg", null, "higiene", 180));
        }

        if (userRepository.findByEmail("cliente@ejemplo.com").isEmpty()) {
            userRepository.save(new User("u1", "cliente@ejemplo.com", "password", "Cliente Demo"));
        }

        if (userRepository.findByEmail("admin@petshop.com").isEmpty()) {
            userRepository.save(new User("u2", "admin@petshop.com", "admin123", "Administrador"));
        }

        if (userRepository.findByEmail("juan@petshop.com").isEmpty()) {
            userRepository.save(new User("u3", "juan@petshop.com", "123456", "Juan Petshop"));
        }
    }
}
