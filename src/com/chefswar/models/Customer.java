package com.chefswar.models;

import com.chefswar.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User{
    private List<Order> orderHistory;

    public Customer(String email, String name, String userId, List<Order> orderHistory) {
        super(UserRole.CUSTOMER, email, name, userId);
        this.orderHistory = orderHistory;
    }

    public void addOrder(Order order) {
        orderHistory.add(order);
    }

    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }



}
