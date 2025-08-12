package com.chefswar.controllers;

import com.chefswar.models.*;
import com.chefswar.patterns.decorator.*;
import com.chefswar.patterns.observer.Restaurant;
import com.chefswar.patterns.strategy.*;
import com.chefswar.service.FoodDeliveryService;
import com.chefswar.utils.InputValidator;
import com.chefswar.utils.OrderProcessor;

import java.util.*;

public class CustomerController {
    private final Scanner scanner;
    private final FoodDeliveryService service;
    private final InputValidator inputValidator;
    private final OrderProcessor orderProcessor;

    public CustomerController(Scanner scanner, FoodDeliveryService service) {
        this.scanner = scanner;
        this.service = service;
        this.inputValidator = new InputValidator(scanner);
        this.orderProcessor = new OrderProcessor(scanner, service);
    } // Added missing closing brace

    public boolean handleCustomerMenu(Customer customer) {
        System.out.println("\n=== Customer Menu ===");
        System.out.println("1. Browse Restaurants");
        System.out.println("2. Place Order");
        System.out.println("3. View Order History");
        System.out.println("4. Rate Dish");
        System.out.println("5. Logout");

        int choice = inputValidator.getIntInput("Choose option: ", 1, 5);

        switch (choice) {
            case 1:
                browseRestaurants();
                return true;
            case 2:
                placeOrder(customer);
                return true;
            case 3:
                viewOrderHistory(customer);
                return true;
            case 4:
                rateDish(customer);
                return true;
            case 5:
                System.out.println("Logged out successfully!");
                return false; // Indicates logout
            default:
                System.out.println("Invalid option!");
                return true;
        }
    }

    private void browseRestaurants() {
        List<Restaurant> restaurants = service.getAvailableRestaurants();
        if (restaurants.isEmpty()) {
            System.out.println("No restaurants available!");
            return;
        }

        System.out.println("\n=== Available Restaurants ===");
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant restaurant = restaurants.get(i);
            System.out.printf("%d. %s (ID: %s)\n", i + 1, restaurant.getName(), restaurant.getRestaurantId());
            System.out.printf("   Menu Items: %d | Revenue: ₹%.2f\n",
                    restaurant.getMenu().size(), restaurant.getTotalRevenue());
        }

        int choice = inputValidator.getIntInput("Select restaurant to view menu (0 to go back): ", 0, restaurants.size());
        if (choice > 0) {
            viewRestaurantMenu(restaurants.get(choice - 1));
        }
    }

    private void viewRestaurantMenu(Restaurant restaurant) {
        System.out.println("\n=== " + restaurant.getName() + " Menu ===");
        Map<String, Dish> menu = restaurant.getMenu();

        if (menu.isEmpty()) {
            System.out.println("No dishes available!");
            return;
        }

        List<Dish> dishes = new ArrayList<>(menu.values());
        for (int i = 0; i < dishes.size(); i++) {
            Dish dish = dishes.get(i);
            System.out.printf("%d. %s - ₹%.2f (Rating: %.1f/5)\n",
                    i + 1, dish.getName(), dish.getBasePrice(), dish.getAverageRating());
            System.out.println("   " + dish.getDescription());
            System.out.println("   Category: " + dish.getCategory());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void placeOrder(Customer customer) {
        List<Restaurant> restaurants = service.getAvailableRestaurants();
        if (restaurants.isEmpty()) {
            System.out.println("No restaurants available!");
            return;
        }

        System.out.println("\n=== Place Order ===");
        System.out.println("Available Restaurants:");
        for (int i = 0; i < restaurants.size(); i++) {
            System.out.println((i + 1) + ". " + restaurants.get(i).getName());
        }

        int restaurantChoice = inputValidator.getIntInput("Select restaurant (enter number): ", 1, restaurants.size()) - 1;

        Restaurant selectedRestaurant = restaurants.get(restaurantChoice);
        Order order = orderProcessor.createOrder(customer.getUserId(), selectedRestaurant.getRestaurantId());

        boolean addingItems = true;
        while (addingItems) {
            System.out.println("\n=== " + selectedRestaurant.getName() + " Menu ===");
            List<Dish> dishes = new ArrayList<>(selectedRestaurant.getMenu().values());

            for (int i = 0; i < dishes.size(); i++) {
                Dish dish = dishes.get(i);
                System.out.printf("%d. %s - ₹%.2f\n", i + 1, dish.getName(), dish.getBasePrice());
            }

            int dishChoice = inputValidator.getIntInput("Select dish to add (0 to finish): ", 0, dishes.size()) - 1;

            if (dishChoice == -1) { // User entered 0
                addingItems = false;
            } else if (dishChoice >= 0 && dishChoice < dishes.size()) {
                Dish selectedDish = dishes.get(dishChoice);
                Customizable customizedDish = orderProcessor.customizeDish(selectedDish);
                order.addItem(customizedDish);
                System.out.println("Added: " + customizedDish.getDescription() + " - ₹" + customizedDish.getPrice());
            }
        }

        if (order.getItems().isEmpty()) {
            System.out.println("No items in order!");
            return;
        }

        // Apply discount strategy
        orderProcessor.applyDiscount(order, selectedRestaurant);

        // Confirm order
        System.out.println("\n" + order.generateBill());
        String confirm = inputValidator.getOptionalStringInput("Confirm order? (y/n): ").toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            String bill = service.placeOrder(order);
            System.out.println("\nOrder placed successfully!");
            System.out.println(bill);
        } else {
            System.out.println("Order cancelled!");
        }
    }

    private void viewOrderHistory(Customer customer) {
        List<Order> orders = customer.getOrderHistory();
        if (orders.isEmpty()) {
            System.out.println("No orders found!");
            return;
        }

        System.out.println("\n=== Order History ===");
        for (Order order : orders) {
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Restaurant: " + service.getRestaurant(order.getRestaurantId())
                    .map(Restaurant::getName).orElse("Unknown"));
            System.out.println("Order Time: " + order.getOrderTime());
            System.out.println("Total Amount: ₹" + order.getTotalAmount());
            System.out.println("Status: " + order.getStatus());
            System.out.println("Items: " + order.getItems().size());
            System.out.println("---");
        }
    }

    private void rateDish(Customer customer) {
        String restaurantId = inputValidator.getStringInput("Enter restaurant ID: ");

        Optional<Restaurant> restaurantOpt = service.getRestaurant(restaurantId);
        if (!restaurantOpt.isPresent()) {
            System.out.println("Restaurant not found!");
            return;
        }

        Restaurant restaurant = restaurantOpt.get();
        System.out.println("\n=== Dishes in " + restaurant.getName() + " ===");
        List<Dish> dishes = new ArrayList<>(restaurant.getMenu().values());

        if (dishes.isEmpty()) {
            System.out.println("No dishes available!");
            return;
        }

        for (int i = 0; i < dishes.size(); i++) {
            Dish dish = dishes.get(i);
            System.out.printf("%d. %s (Current Rating: %.1f/5)\n",
                    i + 1, dish.getName(), dish.getAverageRating());
        }

        int dishChoice = inputValidator.getIntInput("Select dish to rate (enter number): ", 1, dishes.size()) - 1;

        if (dishChoice >= 0 && dishChoice < dishes.size()) {
            Dish selectedDish = dishes.get(dishChoice);
            int rating = inputValidator.getIntInput("Enter rating (1-5): ", 1, 5);
            String comment = inputValidator.getOptionalStringInput("Enter comment (optional): ");

            Rating dishRating = new Rating(customer.getUserId(), selectedDish.getName(), rating, comment);
            selectedDish.addRating(dishRating);

            System.out.println("Rating added successfully!");
            System.out.printf("New average rating for %s: %.1f/5\n",
                    selectedDish.getName(), selectedDish.getAverageRating());
        } else {
            System.out.println("Invalid dish selection!");
        }
    }
}
