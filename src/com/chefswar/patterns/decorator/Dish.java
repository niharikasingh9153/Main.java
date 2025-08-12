package com.chefswar.patterns.decorator;

import com.chefswar.models.Rating;

import java.util.ArrayList;
import java.util.List;

// Base Dish class
public class Dish implements Customizable {
    private String name;
    private String description;
    private double basePrice;
    private String category;
    private List<Rating> ratings;

    public Dish(String name, String description, double basePrice, String category) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.category = category;
        this.ratings = new ArrayList<>();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getPrice() {
        return basePrice;
    }

    public void addRating(Rating rating) {
        ratings.add(rating);
    }

    public double getAverageRating() {
        return ratings.stream()
                .mapToDouble(Rating::getRating)
                .average()
                .orElse(0.0);
    }

    // Getters and setters
    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public String getCategory() { return category; }
    public List<Rating> getRatings() { return new ArrayList<>(ratings); }
}

