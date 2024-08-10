package com.tutorial.ecommerce.apiservice.dto;

import com.tutorial.ecommerce.model.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
