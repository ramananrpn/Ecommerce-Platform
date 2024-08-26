package com.tutorial.ecommerce.model;

public enum Role {
    ADMIN("ROLE_ADMIN"),
    AGENT("ROLE_AGENT"),
    USER("ROLE_USER");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return this.roleName;
    }
}