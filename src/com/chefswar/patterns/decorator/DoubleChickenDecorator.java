package com.chefswar.patterns.decorator;

public class DoubleChickenDecorator extends DishDecorator {
    public DoubleChickenDecorator(Customizable dish) {
        super(dish);
    }

    @Override
    public String getDescription() {
        return dish.getDescription() + ", Double Chicken";
    }

    @Override
    public double getPrice() {
        return dish.getPrice() + 50.0;
    }
}
