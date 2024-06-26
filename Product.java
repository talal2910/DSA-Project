package com.example.inventorymanagementsystem;

public class Product {
    String name;
    int quantity;
    double price;

    public Product(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
    // Override toString for easy printing
    @Override
    public String toString() {
        return "Product: " + name + ", Quantity: " + quantity + ", Price: " + price;
    }
    // JavaFX naming convention for 'name'
    public String getName() {
        return name;
    }

    // JavaFX naming convention for 'quantity'
    public int getQuantity() {
        return quantity;
    }

    // JavaFX naming convention for 'price'
    public double getPrice() {
        return price;
    }

}
