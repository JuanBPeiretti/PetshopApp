package com.petshop.app.model;

public class Product {
    public String id;
    public String name;
    public String brand;
    public double price;
    public Double oldPrice;
    public double rating;
    public String imageUrl;
    public String badge; // e.g., "Oferta", "Nuevo"
    public String categoryId;
    public int stock;

    public Product() {}

    public Product(String id, String name, String brand, double price, Double oldPrice, double rating, String imageUrl, String badge, String categoryId, int stock) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.oldPrice = oldPrice;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.badge = badge;
        this.categoryId = categoryId;
        this.stock = stock;
    }
}
