package com.chefswar.patterns.strategy;

// Concrete Strategies
public class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(double totalAmount) {
        return 0.0;
    }

    @Override
    public String getDiscountDescription() {
        return "No Discount";
    }
}