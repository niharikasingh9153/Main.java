package com.chefswar.patterns.decorator;

// Abstract Decorator
public abstract class DishDecorator implements Customizable {
    protected Customizable dish;

    public DishDecorator(Customizable dish) {
        this.dish = dish;
    }

    @Override
    public String getDescription() {
        return dish.getDescription();
    }

    @Override
    public double getPrice() {
        return dish.getPrice();
    }
}