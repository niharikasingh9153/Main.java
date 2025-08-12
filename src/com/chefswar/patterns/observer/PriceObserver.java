package com.chefswar.patterns.observer;

// Observer interface
public interface PriceObserver {
    void onPriceUpdate(String dishName, double newPrice, String restaurantId);
}