package com.chefswar.patterns.strategy;

public class PercentageDiscountStrategy implements DiscountStrategy {
    private double percentage;

    public PercentageDiscountStrategy(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(double totalAmount) {
        return totalAmount * (percentage / 100);
    }

    @Override
    public String getDiscountDescription() {
        return percentage + "% Discount";
    }
}