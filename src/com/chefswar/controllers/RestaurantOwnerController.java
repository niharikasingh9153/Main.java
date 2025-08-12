package com.chefswar.controllers;

import com.chefswar.models.*;
import com.chefswar.patterns.decorator.Dish;
import com.chefswar.patterns.observer.Restaurant;
import com.chefswar.patterns.strategy.*;
import com.chefswar.service.FoodDeliveryService;
import com.chefswar.utils.InputValidator;

import java.util.*;

public class RestaurantOwnerController {
    private final Scanner scanner;
    private final FoodDeliveryService service;
    private final InputValidator inputValidator;

    public RestaurantOwnerController(Scanner scanner, FoodDeliveryService service) {
        this.scanner = scanner;
        this.service = service;
        this.inputValidator = new InputValidator(scanner);
    } // Added missing closing brace

    public boolean handleRestaurantOwnerMenu(RestaurantOwner owner) {
        System.out.println("\n=== Restaurant Owner Menu ===");
        System.out.println("1. View My Restaurant");
        System.out.println("2. Add Dish");
        System.out.println("3. Remove Dish");
        System.out.println("4. Update Dish Price");
        System.out.println("5. View Dish Ratings");
        System.out.println("6. View Competitor Prices");
        System.out.println("7. Manage Offers");
        System.out.println("8. Logout");

        int choice = inputValidator.getIntInput("Choose option: ", 1, 8);

        Restaurant restaurant = owner.getRestaurant();
        if (restaurant == null && choice != 8) {
            System.out.println("No restaurant assigned!");
            return true;
        }

        switch (choice) {
            case 1:
                viewMyRestaurant(restaurant);
                return true;
            case 2:
                addDish(restaurant);
                return true;
            case 3:
                removeDish(restaurant);
                return true;
            case 4:
                updateDishPrice(restaurant);
                return true;
            case 5:
                viewDishRatings(restaurant);
                return true;
            case 6:
                viewCompetitorPrices(restaurant);
                return true;
            case 7:
                manageOffers(restaurant);
                return true;
            case 8:
                System.out.println("Logged out successfully!");
                return false;
            default:
                System.out.println("Invalid option!");
                return true;
        }
    }

    private void viewMyRestaurant(Restaurant restaurant) {
        System.out.println("\n=== " + restaurant.getName() + " ===");
        System.out.println("Restaurant ID: " + restaurant.getRestaurantId());
        System.out.println("Total Revenue: ₹" + restaurant.getTotalRevenue());
        System.out.println("Status: " + (restaurant.isBlocked() ? "BLOCKED" : "ACTIVE"));
        System.out.println("Menu Items: " + restaurant.getMenu().size());

        if (restaurant.isBlocked()) {
            System.out.println(" Restaurant is blocked! Minimum 5 dishes required.");
        }

        System.out.println("\nMenu:");
        restaurant.getMenu().values().forEach(dish -> {
            System.out.printf("- %s: ₹%.2f (Rating: %.1f/5)\n",
                    dish.getName(), dish.getBasePrice(), dish.getAverageRating());
        });
    }

    private void addDish(Restaurant restaurant) {
        System.out.println("\n=== Add New Dish ===");
        String name = inputValidator.getStringInput("Enter dish name: ");

        if (restaurant.getMenu().containsKey(name)) {
            System.out.println("Dish already exists!");
            return;
        }

        String description = inputValidator.getStringInput("Enter description: ");
        double price = inputValidator.getDoubleInput("Enter price: ", 0.01, 10000.0);
        String category = inputValidator.getStringInput("Enter category: ");

        Dish newDish = new Dish(name, description, price, category);
        restaurant.addDish(newDish);

        System.out.println("Dish added successfully!");
        if (!restaurant.isBlocked()) {
            System.out.println("Restaurant is now active!");
        }
    }

    private void removeDish(Restaurant restaurant) {
        if (restaurant.getMenu().isEmpty()) {
            System.out.println("No dishes available to remove!");
            return;
        }

        System.out.println("\n=== Remove Dish ===");
        List<Dish> dishes = new ArrayList<>(restaurant.getMenu().values());

        for (int i = 0; i < dishes.size(); i++) {
            System.out.printf("%d. %s - ₹%.2f\n", i + 1, dishes.get(i).getName(), dishes.get(i).getBasePrice());
        }

        int choice = inputValidator.getIntInput("Select dish to remove (enter number): ", 1, dishes.size()) - 1;

        if (choice >= 0 && choice < dishes.size()) {
            Dish dishToRemove = dishes.get(choice);
            restaurant.removeDish(dishToRemove.getName());

            System.out.println("Dish removed successfully!");
            if (restaurant.isBlocked()) {
                System.out.println(" Warning: Restaurant is now blocked! Add more dishes to reach minimum of 5.");
            }
        } else {
            System.out.println("Invalid selection!");
        }
    }

    private void updateDishPrice(Restaurant restaurant) {
        if (restaurant.getMenu().isEmpty()) {
            System.out.println("No dishes available!");
            return;
        }

        System.out.println("\n=== Update Dish Price ===");
        List<Dish> dishes = new ArrayList<>(restaurant.getMenu().values());

        for (int i = 0; i < dishes.size(); i++) {
            System.out.printf("%d. %s - ₹%.2f\n", i + 1, dishes.get(i).getName(), dishes.get(i).getBasePrice());
        }

        int choice = inputValidator.getIntInput("Select dish to update price (enter number): ", 1, dishes.size()) - 1;

        if (choice >= 0 && choice < dishes.size()) {
            Dish selectedDish = dishes.get(choice);
            System.out.printf("Current price: ₹%.2f\n", selectedDish.getBasePrice());

            double newPrice = inputValidator.getDoubleInput("Enter new price: ", 0.01, 10000.0);
            double oldPrice = selectedDish.getBasePrice();

            restaurant.updateDishPrice(selectedDish.getName(), newPrice);
            System.out.printf("Price updated from ₹%.2f to ₹%.2f\n", oldPrice, newPrice);

            // Check if this triggers price war
            if (oldPrice > newPrice && (oldPrice - newPrice) / oldPrice > 0.15) {
                System.out.println(" Price war triggered! Competitors will adjust their prices.");
            }
        } else {
            System.out.println("Invalid selection!");
        }
    }

    private void viewDishRatings(Restaurant restaurant) {
        if (restaurant.getMenu().isEmpty()) {
            System.out.println("No dishes available!");
            return;
        }

        System.out.println("\n=== Dish Ratings ===");
        restaurant.getMenu().values().forEach(dish -> {
            System.out.printf("\n%s (₹%.2f)\n", dish.getName(), dish.getBasePrice());
            System.out.printf("Average Rating: %.1f/5 (%d reviews)\n",
                    dish.getAverageRating(), dish.getRatings().size());

            if (!dish.getRatings().isEmpty()) {
                System.out.println("Recent Reviews:");
                dish.getRatings().stream()
                        .sorted((r1, r2) -> r2.getRatingTime().compareTo(r1.getRatingTime()))
                        .limit(3)
                        .forEach(rating -> {
                            System.out.printf("  %d/5 - %s (by %s)\n",
                                    rating.getRating(), rating.getComment(), rating.getCustomerId());
                        });
            }
        });
    }

    private void viewCompetitorPrices(Restaurant myRestaurant) {
        List<Restaurant> competitors = service.getAvailableRestaurants().stream()
                .filter(r -> !r.getRestaurantId().equals(myRestaurant.getRestaurantId()))
                .collect(java.util.stream.Collectors.toList());

        if (competitors.isEmpty()) {
            System.out.println("No competitors found!");
            return;
        }

        System.out.println("\n=== Competitor Price Analysis ===");

        // Get all unique dishes across all restaurants
        Set<String> allDishes = new HashSet<>();
        allDishes.addAll(myRestaurant.getMenu().keySet());
        competitors.forEach(r -> allDishes.addAll(r.getMenu().keySet()));

        for (String dishName : allDishes) {
            System.out.println("\n" + dishName + ":");

            // My restaurant price
            Dish myDish = myRestaurant.getMenu().get(dishName);
            if (myDish != null) {
                System.out.printf("  My Restaurant: ₹%.2f\n", myDish.getBasePrice());
            } else {
                System.out.println("  My Restaurant: Not Available");
            }

            // Competitor prices
            competitors.forEach(competitor -> {
                Dish competitorDish = competitor.getMenu().get(dishName);
                if (competitorDish != null) {
                    System.out.printf("  %s: ₹%.2f\n",
                            competitor.getName(), competitorDish.getBasePrice());
                } else {
                    System.out.printf("  %s: Not Available\n", competitor.getName());
                }
            });
        }
    }

    private void manageOffers(Restaurant restaurant) {
        System.out.println("\n=== Manage Restaurant Offers ===");
        System.out.println("1. Set Percentage Discount");
        System.out.println("2. Set Flat Discount");
        System.out.println("3. Remove Current Offers");
        System.out.println("4. View Current Offers");
        System.out.println("5. Back to Main Menu");

        int choice = inputValidator.getIntInput("Choose option: ", 1, 5);

        switch (choice) {
            case 1:
                setPercentageOffer(restaurant);
                break;
            case 2:
                setFlatOffer(restaurant);
                break;
            case 3:
                removeOffers(restaurant);
                break;
            case 4:
                viewCurrentOffers(restaurant);
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid option!");
        }
    }

    private void setPercentageOffer(Restaurant restaurant) {
        double percentage = inputValidator.getDoubleInput("Enter percentage discount (1-50): ", 1.0, 50.0);

        restaurant.setCurrentOffer(new PercentageDiscountStrategy(percentage));
        System.out.printf(" Set %.1f%% discount offer for %s\n", percentage, restaurant.getName());
        System.out.println("This offer will be available to customers placing orders from your restaurant.");
    }

    private void setFlatOffer(Restaurant restaurant) {
        double amount = inputValidator.getDoubleInput("Enter flat discount amount (₹): ", 0.01, 1000.0);

        restaurant.setCurrentOffer(new FlatDiscountStrategy(amount));
        System.out.printf(" Set flat ₹%.2f discount offer for %s\n", amount, restaurant.getName());
        System.out.println("This offer will be available to customers placing orders from your restaurant.");
    }

    private void removeOffers(Restaurant restaurant) {
        restaurant.setCurrentOffer(new NoDiscountStrategy());
        System.out.println(" All offers removed for " + restaurant.getName());
    }

    private void viewCurrentOffers(Restaurant restaurant) {
        DiscountStrategy currentOffer = restaurant.getCurrentOffer();
        System.out.println("Current offer: " + currentOffer.getDiscountDescription());
    }
}
