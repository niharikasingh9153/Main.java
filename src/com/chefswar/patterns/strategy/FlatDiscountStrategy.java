package com.chefswar.patterns.strategy;

public class FlatDiscountStrategy implements DiscountStrategy {
    private double flatAmount;

    public FlatDiscountStrategy(double flatAmount) {
        this.flatAmount = flatAmount;
    }

    @Override
    public double calculateDiscount(double totalAmount) {
        return Math.min(flatAmount, totalAmount);
    }

    @Override
    public String getDiscountDescription() {
        return "Flat ₹" + flatAmount + " Off";
    }
}
