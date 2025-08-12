package com.chefswar.patterns.observer;

import com.chefswar.models.RestaurantOwner;
import com.chefswar.patterns.decorator.Dish;
import com.chefswar.patterns.strategy.DiscountStrategy;
import com.chefswar.patterns.strategy.NoDiscountStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Restaurant implementing Observer pattern
public class Restaurant implements PriceObserver, PriceSubject {
    private String restaurantId;
    private String name;
    private RestaurantOwner owner;
    private Map<String, Dish> menu;
    private List<PriceObserver> observers;
    private boolean isBlocked;
    private double totalRevenue;
    private DiscountStrategy currentOffer;

    public Restaurant(String restaurantId, String name, RestaurantOwner owner) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.owner = owner;
        this.menu = new HashMap<>();
        this.observers = new ArrayList<>();
        this.isBlocked = false;
        this.totalRevenue = 0.0;
        this.currentOffer = new NoDiscountStrategy();
    }

    public void setCurrentOffer(DiscountStrategy offer) {
        this.currentOffer = offer;
    }

    public DiscountStrategy getCurrentOffer() {
        return currentOffer;
    }

    public void addDish(Dish dish) {
        menu.put(dish.getName(), dish);
        checkMenuSizeAndBlock();
    }

    public void removeDish(String dishName) {
        menu.remove(dishName);
        checkMenuSizeAndBlock();
    }

    private void checkMenuSizeAndBlock() {
        if (menu.size() < 5) {
            this.isBlocked = true;
        } else {
            this.isBlocked = false;
        }
    }

    public void updateDishPrice(String dishName, double newPrice) {
        Dish dish = menu.get(dishName);
        if (dish != null) {
            double oldPrice = dish.getBasePrice();
            dish.setBasePrice(newPrice);

            // Check if price reduction is more than 15%
            if (oldPrice > newPrice && (oldPrice - newPrice) / oldPrice > 0.15) {
                notifyObservers(dishName, newPrice);
            }
        }
    }

    @Override
    public void onPriceUpdate(String dishName, double competitorPrice, String competitorRestaurantId) {
        if (!competitorRestaurantId.equals(this.restaurantId) && menu.containsKey(dishName)) {
            Dish dish = menu.get(dishName);
            double currentPrice = dish.getBasePrice();
            double newPrice = currentPrice - (currentPrice * 0.05);
            dish.setBasePrice(Math.max(newPrice, 1.0)); // Minimum price of 1
        }
    }

    @Override
    public void addObserver(PriceObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(PriceObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String dishName, double newPrice) {
        observers.forEach(observer -> observer.onPriceUpdate(dishName, newPrice, this.restaurantId));
    }

    // Getters and setters
    public String getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public Map<String, Dish> getMenu() { return new HashMap<>(menu); }
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { this.isBlocked = blocked; }
    public double getTotalRevenue() { return totalRevenue; }
    public void addRevenue(double amount) { this.totalRevenue += amount; }
}
