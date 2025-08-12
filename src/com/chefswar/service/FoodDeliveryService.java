package com.chefswar.service;

import com.chefswar.models.Customer;
import com.chefswar.models.Order;
import com.chefswar.models.User;
import com.chefswar.patterns.decorator.Customizable;
import com.chefswar.patterns.decorator.Dish;
import com.chefswar.patterns.observer.Restaurant;

import java.util.*;
import java.util.stream.Collectors;

public class FoodDeliveryService {
    private Map<String, User> users;
    private Map<String, Restaurant> restaurants;
    private Map<String, Order> orders;
    private static FoodDeliveryService instance;

    private FoodDeliveryService() {
        users = new HashMap<>();
        restaurants = new HashMap<>();
        orders = new HashMap<>();
    }

    public static FoodDeliveryService getInstance() {
        if (instance == null) {
            instance = new FoodDeliveryService();
        }
        return instance;
    }

    public void registerUser(User user) {
        users.put(user.getUserId(), user);
    }

    public void registerRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getRestaurantId(), restaurant);

        // Add this restaurant as observer to all existing restaurants
        restaurants.values().forEach(existingRestaurant -> {
            if (!existingRestaurant.getRestaurantId().equals(restaurant.getRestaurantId())) {
                existingRestaurant.addObserver(restaurant);
                restaurant.addObserver(existingRestaurant);
            }
        });
    }

    public Optional<User> getUser(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Optional<Restaurant> getRestaurant(String restaurantId) {
        return Optional.ofNullable(restaurants.get(restaurantId));
    }

    public Map<String, Restaurant> getAllRestaurants() {
        return new HashMap<>(restaurants);
    }

    public Map<String, Order> getAllOrders() {
        return new HashMap<>(orders);
    }

    public List<Restaurant> getAvailableRestaurants() {
        return restaurants.values().stream()
                .filter(restaurant -> !restaurant.isBlocked())
                .collect(Collectors.toList());
    }

    public String placeOrder(Order order) {
        orders.put(order.getOrderId(), order);

        // Add revenue to restaurant
        Restaurant restaurant = restaurants.get(order.getRestaurantId());
        if (restaurant != null) {
            restaurant.addRevenue(order.getTotalAmount());
        }

        // Add order to customer history
        Customer customer = (Customer) users.get(order.getCustomerId());
        if (customer != null) {
            customer.addOrder(order);
        }

        return order.generateBill();
    }

    // Analytics methods
    public List<String> getTop3MostOrderedDishes() {
        return orders.values().stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> {
                            // Extract base dish name (remove customizations)
                            String description = item.getDescription();
                            String[] parts = description.split(",");
                            return parts[0].trim();
                        },
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Optional<Restaurant> getRestaurantWithHighestRevenue() {
        return restaurants.values().stream()
                .max(Comparator.comparingDouble(Restaurant::getTotalRevenue));
    }

    public Optional<String> getDishWithHighestAverageRating() {
        return restaurants.values().stream()
                .flatMap(restaurant -> restaurant.getMenu().values().stream())
                .filter(dish -> dish.getAverageRating() > 0)
                .max(Comparator.comparingDouble(Dish::getAverageRating))
                .map(dish -> dish.getName() + " (Rating: " +
                        String.format("%.1f", dish.getAverageRating()) + "/5)");
    }
}
