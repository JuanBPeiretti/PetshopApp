package com.petshop.app.dto;

import com.petshop.app.model.User;

public class UserDTO {
    public String id;
    public String email;
    public String name;

    public UserDTO() {}

    public UserDTO(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public static UserDTO fromUser(User user) {
        return new UserDTO(user.id, user.email, user.name);
    }
}
