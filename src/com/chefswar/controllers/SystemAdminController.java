package com.chefswar.controllers;

import com.chefswar.models.*;
import com.chefswar.patterns.decorator.Dish;
import com.chefswar.patterns.observer.Restaurant;
import com.chefswar.service.FoodDeliveryService;
import com.chefswar.utils.InputValidator;

import java.util.*;
import java.util.stream.Collectors;

public class SystemAdminController {
    private final Scanner scanner;
    private final FoodDeliveryService service;
    private final InputValidator inputValidator;

    public SystemAdminController(Scanner scanner, FoodDeliveryService service) {
        this.scanner = scanner;
        this.service = service;
        this.inputValidator = new InputValidator(scanner);
    } // Added missing closing brace

    public boolean handleSystemAdminMenu(SystemAdmin admin) {
        System.out.println("\n=== System Admin Menu ===");
        System.out.println("1. View All Restaurants");
        System.out.println("2. Block/Unblock Restaurant");
        System.out.println("3. View Analytics");
        System.out.println("4. Logout");

        int choice = inputValidator.getIntInput("Choose option: ", 1, 4);

        switch (choice) {
            case 1:
                viewAllRestaurants();
                return true;
            case 2:
                blockUnblockRestaurant();
                return true;
            case 3:
                viewComprehensiveAnalytics();
                return true;
            case 4:
                System.out.println("Logged out successfully!");
                return false;
            default:
                System.out.println("Invalid option!");
                return true;
        }
    }

    private void viewComprehensiveAnalytics() {
        System.out.println("\n=== Comprehensive System Analytics ===");

        // Top 3 most ordered dishes
        List<String> topDishes = service.getTop3MostOrderedDishes();
        System.out.println("\n Top 3 Most Ordered Dishes:");
        if (topDishes.isEmpty()) {
            System.out.println("No orders placed yet!");
        } else {
            for (int i = 0; i < topDishes.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, topDishes.get(i));
            }
        }

        // Restaurant with highest revenue
        Optional<Restaurant> topRevenueRestaurant = service.getRestaurantWithHighestRevenue();
        System.out.println("\n Restaurant with Highest Revenue:");
        if (topRevenueRestaurant.isPresent()) {
            Restaurant restaurant = topRevenueRestaurant.get();
            System.out.printf("%s - ₹%.2f\n", restaurant.getName(), restaurant.getTotalRevenue());
        } else {
            System.out.println("No revenue data available!");
        }

        // Dish with highest average rating
        Optional<String> topRatedDish = service.getDishWithHighestAverageRating();
        System.out.println("\n Dish with Highest Average Rating:");
        if (topRatedDish.isPresent()) {
            System.out.println(topRatedDish.get());
        } else {
            System.out.println("No ratings available!");
        }

        // Additional analytics
        System.out.println("\n Additional System Metrics:");
        viewRevenueReport();
        viewTopDishes();
    }

    private void viewAllRestaurants() {
        Map<String, Restaurant> allRestaurants = service.getAllRestaurants();
        if (allRestaurants.isEmpty()) {
            System.out.println("No restaurants registered!");
            return;
        }

        System.out.println("\n=== All Restaurants ===");
        allRestaurants.values().forEach(restaurant -> {
            System.out.printf("%s (ID: %s)\n", restaurant.getName(), restaurant.getRestaurantId());
            System.out.printf("  Status: %s\n", restaurant.isBlocked() ? "BLOCKED" : "ACTIVE");
            System.out.printf("  Menu Items: %d\n", restaurant.getMenu().size());
            System.out.printf("  Total Revenue: ₹%.2f\n", restaurant.getTotalRevenue());
            System.out.println("  ---");
        });
    }

    private void blockUnblockRestaurant() {
        Map<String, Restaurant> allRestaurants = service.getAllRestaurants();
        if (allRestaurants.isEmpty()) {
            System.out.println("No restaurants found!");
            return;
        }

        System.out.println("\n=== Block/Unblock Restaurant ===");
        List<Restaurant> restaurants = new ArrayList<>(allRestaurants.values());

        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant restaurant = restaurants.get(i);
            System.out.printf("%d. %s - %s\n",
                    i + 1, restaurant.getName(), restaurant.isBlocked() ? "BLOCKED" : "ACTIVE");
        }

        int choice = inputValidator.getIntInput("Select restaurant (enter number): ", 1, restaurants.size()) - 1;

        if (choice >= 0 && choice < restaurants.size()) {
            Restaurant selectedRestaurant = restaurants.get(choice);

            if (selectedRestaurant.isBlocked()) {
                selectedRestaurant.setBlocked(false);
                System.out.println("Restaurant unblocked successfully!");
            } else {
                selectedRestaurant.setBlocked(true);
                System.out.println("Restaurant blocked successfully!");
            }
        } else {
            System.out.println("Invalid selection!");
        }
    }

    private void viewTopDishes() {
        System.out.println("\n=== Top Dishes Analysis ===");

        Map<String, Restaurant> allRestaurants = service.getAllRestaurants();
        List<Dish> allDishes = allRestaurants.values().stream()
                .flatMap(restaurant -> restaurant.getMenu().values().stream())
                .collect(Collectors.toList());

        if (allDishes.isEmpty()) {
            System.out.println("No dishes found!");
            return;
        }

        // Top rated dishes
        System.out.println("\nTop Rated Dishes:");
        allDishes.stream()
                .filter(dish -> dish.getAverageRating() > 0)
                .sorted((d1, d2) -> Double.compare(d2.getAverageRating(), d1.getAverageRating()))
                .limit(5)
                .forEach(dish -> {
                    System.out.printf("%s - %.1f/5 (%d reviews)\n",
                            dish.getName(), dish.getAverageRating(), dish.getRatings().size());
                });

        // Most expensive dishes
        System.out.println("\nMost Expensive Dishes:");
        allDishes.stream()
                .sorted((d1, d2) -> Double.compare(d2.getBasePrice(), d1.getBasePrice()))
                .limit(5)
                .forEach(dish -> {
                    System.out.printf("%s - ₹%.2f\n", dish.getName(), dish.getBasePrice());
                });
    }

    private void viewRevenueReport() {
        System.out.println("\n=== Revenue Report ===");

        Map<String, Restaurant> allRestaurants = service.getAllRestaurants();
        if (allRestaurants.isEmpty()) {
            System.out.println("No restaurants found!");
            return;
        }

        double totalSystemRevenue = allRestaurants.values().stream()
                .mapToDouble(Restaurant::getTotalRevenue)
                .sum();

        System.out.printf("Total System Revenue: ₹%.2f\n\n", totalSystemRevenue);

        System.out.println("Restaurant-wise Revenue:");
        allRestaurants.values().stream()
                .sorted((r1, r2) -> Double.compare(r2.getTotalRevenue(), r1.getTotalRevenue()))
                .forEach(restaurant -> {
                    double percentage = totalSystemRevenue > 0 ?
                            (restaurant.getTotalRevenue() / totalSystemRevenue) * 100 : 0;
                    System.out.printf("%s: ₹%.2f (%.1f%%)\n",
                            restaurant.getName(), restaurant.getTotalRevenue(), percentage);
                });

        // Orders count
        Map<String, Order> allOrders = service.getAllOrders();
        System.out.printf("\nTotal Orders: %d\n", allOrders.size());

        if (!allOrders.isEmpty()) {
            double averageOrderValue = allOrders.values().stream()
                    .mapToDouble(Order::getTotalAmount)
                    .average()
                    .orElse(0.0);
            System.out.printf("Average Order Value: ₹%.2f\n", averageOrderValue);
        }
    }
}
