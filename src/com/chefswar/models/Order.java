package com.chefswar.models;

import com.chefswar.enums.OrderStatus;
import com.chefswar.patterns.decorator.Customizable;
import com.chefswar.patterns.strategy.DiscountStrategy;
import com.chefswar.patterns.strategy.NoDiscountStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private String restaurantId;
    private List<Customizable> items;
    private OrderStatus status;
    private LocalDateTime orderTime;
    private double totalAmount;
    private DiscountStrategy discountStrategy;

    public Order(String orderId, String customerId, String restaurantId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.orderTime = LocalDateTime.now();
        this.discountStrategy = new NoDiscountStrategy();
    }

    public void addItem(Customizable item) {
        items.add(item);
        calculateTotal();
    }

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
        calculateTotal();
    }

    private void calculateTotal() {
        double baseAmount = items.stream()
                .mapToDouble(Customizable::getPrice)
                .sum();

        double discount = discountStrategy.calculateDiscount(baseAmount);
        double afterDiscount = baseAmount - discount;
        double tax = afterDiscount * 0.05; // 5% GST

        this.totalAmount = afterDiscount + tax;
    }

    public String generateBill() {
        StringBuilder bill = new StringBuilder();
        bill.append("================ BILL ================\n");
        bill.append("Order ID: ").append(orderId).append("\n");
        bill.append("Customer ID: ").append(customerId).append("\n");
        bill.append("Restaurant ID: ").append(restaurantId).append("\n");
        bill.append("Order Time: ").append(orderTime).append("\n\n");

        double baseAmount = 0;
        for (Customizable item : items) {
            bill.append(item.getDescription()).append(" - ₹").append(item.getPrice()).append("\n");
            baseAmount += item.getPrice();
        }

        double discount = discountStrategy.calculateDiscount(baseAmount);
        double afterDiscount = baseAmount - discount;
        double tax = afterDiscount * 0.05;

        bill.append("\n");
        bill.append("Base Amount: ₹").append(baseAmount).append("\n");
        bill.append("Discount (").append(discountStrategy.getDiscountDescription()).append("): -₹").append(discount).append("\n");
        bill.append("After Discount: ₹").append(afterDiscount).append("\n");
        bill.append("GST (5%): ₹").append(tax).append("\n");
        bill.append("Total Amount: ₹").append(totalAmount).append("\n");
        bill.append("=====================================");

        return bill.toString();
    }

    // Getters and setters
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getRestaurantId() { return restaurantId; }
    public List<Customizable> getItems() { return new ArrayList<>(items); }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public double getTotalAmount() { return totalAmount; }
}
