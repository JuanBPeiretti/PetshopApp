package com.petshop.app.service;

import com.petshop.app.model.Category;
import com.petshop.app.model.Product;
import com.petshop.app.model.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements InitializingBean {

    private final InMemoryStore store;

    public DataLoader(InMemoryStore store) {
        this.store = store;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        store.categories.add(new Category("perros","Perros","#FFD6A5"));
        store.categories.add(new Category("gatos","Gatos","#BFEAF5"));
        store.categories.add(new Category("alimentos","Alimentos","#FFE6CC"));
        store.categories.add(new Category("juguetes","Juguetes","#FDE2E4"));

        store.products.add(new Product("p1","Croquetas SuperDog 10kg","SuperDog",12999.0,15999.0,4.6, productImage("dog-food"),"Oferta","alimentos",50));
        store.products.add(new Product("p2","Pelota Squeaky","PetPlay",499.0,null,4.3, productImage("dog-toy-ball"),null,"juguetes",200));
        store.products.add(new Product("p3","Collar Comfort L","WalkEasy",899.0,1099.0,4.5, productImage("dog-collar"),"Más vendido","accesorios",120));
        store.products.add(new Product("p4","Arena para gato Premium 8L","GatoClean",649.0,799.0,4.4, productImage("cat-litter"),"Oferta","alimentos",80));

        // Demo user
        User u = new User("u1","cliente@ejemplo.com","password","Cliente Demo");
        store.users.put(u.email,u);
    }

    private String productImage(String key) {
        String[] gallery = {
                "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1511044568932-338cba0ad803?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1533738363-b7f9aef128ce?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1526336024174-e58f5cdd8e13?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1570018143030-1f5f0f6cbeb7?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1425082661705-1834bfd09dca?auto=format&fit=crop&w=900&q=80"
        };

        int hash = Math.abs(key.hashCode());
        return gallery[hash % gallery.length];
    }
}
