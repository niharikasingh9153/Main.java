package com.chefswar.patterns.observer;

// Subject interface
public interface PriceSubject {
    void addObserver(PriceObserver observer);
    void removeObserver(PriceObserver observer);
    void notifyObservers(String dishName, double newPrice);
}