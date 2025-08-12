package com.chefswar.patterns.decorator;

// Concrete Decorators
public class ExtraCheeseDecorator extends DishDecorator {
    public ExtraCheeseDecorator(Customizable dish) {
        super(dish);
    }

    @Override
    public String getDescription() {
        return dish.getDescription() + ", Extra Cheese";
    }

    @Override
    public double getPrice() {
        return dish.getPrice() + 25.0;
    }
}