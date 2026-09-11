package com.petshop.app.model;

public class Category {
    public String id;
    public String name;
    public String color; // hex or css

    public Category() {}

    public Category(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }
}
