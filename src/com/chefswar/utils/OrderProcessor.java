package com.chefswar.utils;

import com.chefswar.models.*;
import com.chefswar.patterns.decorator.*;
import com.chefswar.patterns.observer.Restaurant;
import com.chefswar.patterns.strategy.*;
import com.chefswar.service.FoodDeliveryService;

import java.util.Scanner;

public class OrderProcessor {
    private final Scanner scanner;
    private final FoodDeliveryService service;
    private final com.chefswar.utils.InputValidator inputValidator;

    public OrderProcessor(Scanner scanner, FoodDeliveryService service) {
        this.scanner = scanner;
        this.service = service;
        this.inputValidator = new com.chefswar.utils.InputValidator(scanner);
    }

    public Customizable customizeDish(Dish dish) {
        Customizable customizedDish = dish;

        System.out.println("\n=== Customize " + dish.getName() + " ===");
        System.out.println("Available customizations:");
        System.out.println("1. Extra Cheese (+₹25)");
        System.out.println("2. Double Chicken (+₹50)");
        System.out.println("3. Spicy Sauce (+₹15)");
        System.out.println("4. No customization");

        while (true) {
            int choice = inputValidator.getIntInput("Select customization (4 when done): ", 1, 4);

            switch (choice) {
                case 1:
                    customizedDish = new ExtraCheeseDecorator(customizedDish);
                    System.out.println("Added Extra Cheese");
                    break;
                case 2:
                    customizedDish = new DoubleChickenDecorator(customizedDish);
                    System.out.println("Added Double Chicken");
                    break;
                case 3:
                    customizedDish = new SpicySauceDecorator(customizedDish);
                    System.out.println("Added Spicy Sauce");
                    break;
                case 4:
                    return customizedDish;
            }
        }
    }

    public void applyDiscount(Order order, Restaurant restaurant) {
        System.out.println("\n=== Available Offers ===");
        DiscountStrategy restaurantOffer = restaurant.getCurrentOffer();
        System.out.println("1. " + restaurantOffer.getDiscountDescription() + " (Restaurant Offer)");
        System.out.println("2. No Discount");

        int choice = inputValidator.getIntInput("Select offer: ", 1, 2);

        switch (choice) {
            case 1:
                order.setDiscountStrategy(restaurantOffer);
                break;
            case 2:
                order.setDiscountStrategy(new NoDiscountStrategy());
                break;
        }
    }

    // Helper method to create order
    public Order createOrder(String customerId, String restaurantId) {
        return new Order(
                "ORD" + System.currentTimeMillis(),
                customerId,
                restaurantId
        );
    }
}
