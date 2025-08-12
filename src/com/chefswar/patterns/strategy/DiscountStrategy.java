package com.chefswar.patterns.strategy;

// Strategy interface
public interface DiscountStrategy {
    double calculateDiscount(double totalAmount);
    String getDiscountDescription();
}