package com.petshop.app.model;

public class User {
    public String id;
    public String email;
    public String password; // plaintext for demo only
    public String name;

    public User() {}

    public User(String id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
