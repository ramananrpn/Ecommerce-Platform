package com.tutorial.ecommerce.apiservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(min = 3, max = 20)
    private String username;

    @Size(min = 8)
    private String password;

    private String role;

    private String email;
}
