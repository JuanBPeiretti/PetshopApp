package com.petshop.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {

    @Id
    private String id;

    private String name;

    private String brand;

    private double price;

    private Double oldPrice;

    private double rating;

    private String imageUrl;

    private String badge;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private int stock;

    public Product() {
    }

    public Product(String id, String name, String brand, double price,
                   Double oldPrice, double rating, String imageUrl,
                   String badge, Category category, int stock) {

        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.oldPrice = oldPrice;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.badge = badge;
        this.category = category;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(Double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}