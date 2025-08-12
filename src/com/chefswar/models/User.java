package com.chefswar.models;

import com.chefswar.enums.UserRole;

public abstract class User {
    protected String userId;
    protected String name;
    protected String email;
    protected UserRole role;

    public User(UserRole role, String email, String name, String userId) {
        this.role = role;
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
