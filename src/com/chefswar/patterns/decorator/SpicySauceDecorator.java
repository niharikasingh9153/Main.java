package com.chefswar.patterns.decorator;

public class SpicySauceDecorator extends DishDecorator {
    public SpicySauceDecorator(Customizable dish) {
        super(dish);
    }

    @Override
    public String getDescription() {
        return dish.getDescription() + ", Spicy Sauce";
    }

    @Override
    public double getPrice() {
        return dish.getPrice() + 15.0;
    }
}