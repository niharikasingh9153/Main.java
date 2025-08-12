package com.chefswar.models;

import com.chefswar.enums.UserRole;
import com.chefswar.patterns.observer.Restaurant;

public class RestaurantOwner extends User{
    private Restaurant restaurant;

    public RestaurantOwner(String email, String name, String userId, Restaurant restaurant) {
        super(UserRole.RESTAURANT_OWNER, email, name, userId);
        this.restaurant = restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }
}
