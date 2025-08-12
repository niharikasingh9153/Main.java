package com.chefswar.models;

import java.time.LocalDateTime;

public class Rating {
    private String customerId;
    private String dishName;
    private int rating; // 1-5 stars
    private String comment;
    private LocalDateTime ratingTime;

    public Rating(String customerId, String dishName, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.customerId = customerId;
        this.dishName = dishName;
        this.rating = rating;
        this.comment = comment;
        this.ratingTime = LocalDateTime.now();
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getDishName() { return dishName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getRatingTime() { return ratingTime; }
}
