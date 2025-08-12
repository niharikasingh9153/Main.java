package com.chefswar.utils;

import com.chefswar.models.*;
import com.chefswar.patterns.decorator.Dish;
import com.chefswar.patterns.observer.Restaurant;
import com.chefswar.service.FoodDeliveryService;

import java.util.ArrayList;

public class DataInitializer {

    public static void initializeData(FoodDeliveryService service) {
        // Create sample users
        Customer customer1 = new Customer("john@email.com", "John Doe", "C001", new ArrayList<>());
        Customer customer2 = new Customer("jane@email.com", "Jane Smith", "C002", new ArrayList<>());
        RestaurantOwner owner1 = new RestaurantOwner("alice@email.com", "Alice Smith", "O001", null);
        RestaurantOwner owner2 = new RestaurantOwner("bob@email.com", "Bob Wilson", "O002", null);
        SystemAdmin admin = new SystemAdmin("admin@email.com", "Admin", "A001");

        service.registerUser(customer1);
        service.registerUser(customer2);
        service.registerUser(owner1);
        service.registerUser(owner2);
        service.registerUser(admin);

        // Create restaurants
        Restaurant restaurant1 = new Restaurant("R001", "Pizza Palace", owner1);
        Restaurant restaurant2 = new Restaurant("R002", "Burger Kingdom", owner2);

        owner1.setRestaurant(restaurant1);
        owner2.setRestaurant(restaurant2);

        // Add dishes
        initializeRestaurantMenus(restaurant1, restaurant2);

        service.registerRestaurant(restaurant1);
        service.registerRestaurant(restaurant2);

        // Add sample ratings
        addSampleRatings(restaurant1);
    }

    private static void initializeRestaurantMenus(Restaurant restaurant1, Restaurant restaurant2) {
        // Restaurant 1 dishes
        restaurant1.addDish(new Dish("Margherita Pizza", "Classic pizza with tomato and cheese", 299.0, "Pizza"));
        restaurant1.addDish(new Dish("Pepperoni Pizza", "Pizza with pepperoni", 399.0, "Pizza"));
        restaurant1.addDish(new Dish("Chicken Burger", "Grilled chicken burger", 249.0, "Burger"));
        restaurant1.addDish(new Dish("Pasta Alfredo", "Creamy pasta", 329.0, "Pasta"));
        restaurant1.addDish(new Dish("Caesar Salad", "Fresh caesar salad", 199.0, "Salad"));

        // Restaurant 2 dishes
        restaurant2.addDish(new Dish("Classic Burger", "Beef burger with lettuce and tomato", 199.0, "Burger"));
        restaurant2.addDish(new Dish("Chicken Burger", "Grilled chicken burger", 229.0, "Burger"));
        restaurant2.addDish(new Dish("Fish Burger", "Fresh fish burger", 259.0, "Burger"));
        restaurant2.addDish(new Dish("Veggie Burger", "Vegetarian burger", 179.0, "Burger"));
        restaurant2.addDish(new Dish("French Fries", "Crispy french fries", 99.0, "Sides"));
    }

    private static void addSampleRatings(Restaurant restaurant1) {
        Dish margherita = restaurant1.getMenu().get("Margherita Pizza");
        if (margherita != null) {
            margherita.addRating(new Rating("C001", "Margherita Pizza", 4, "Great taste!"));
            margherita.addRating(new Rating("C002", "Margherita Pizza", 5, "Excellent!"));
        }
    }
}
