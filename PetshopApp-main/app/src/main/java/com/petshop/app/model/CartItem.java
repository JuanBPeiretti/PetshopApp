package com.petshop.app.model;

public class CartItem {
    public String productId;
    public String name;
    public String variant;
    public int quantity;
    public double price;

    public CartItem() {}

    public CartItem(String productId, String name, String variant, int quantity, double price) {
        this.productId = productId;
        this.name = name;
        this.variant = variant;
        this.quantity = quantity;
        this.price = price;
    }
}
