package com.example.inventorymanagementsystem;

import java.util.*;
import java.io.*;
public class Inventory {
    public static final int REORDER_THRESHOLD=10;
    LinkedList<Product> products = new LinkedList<>();
    Queue<Product> orderQueue = new LinkedList<>();

    Stack<Product> salesHistory = new Stack<>();
    Queue<Product> reorderQueue = new LinkedList<>();

    // Add product to the inventory
    public void addProduct(Product product) {
        products.add(product);
    }

    // Update product details
    public void updateProduct(String name, int newQuantity, double newPrice) {
        for (Product product : products) {
            if (product.name.equals(name)) {
                product.quantity = newQuantity;
                product.price = newPrice;
                System.out.println("Product updated");
                break;
            }
        }
    }

    // Search for a product by name
    public Product searchProduct(String name) {
        for (Product product : products) {
            if (product.name.equals(name)) {
                return product;
            }
        }
        return null;
    }

    public void saveToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Product product : products) {
                // Assuming Product has a meaningful toString() method
                writer.write(product.toString());
                writer.newLine();  // Add a new line for each product
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to add an order to the queue
    public void addOrder(Product order) {
        orderQueue.add(order);
    }

    public void processOrders() {
        while (!orderQueue.isEmpty()) {
            Product order = orderQueue.poll();
            boolean productFound = false;

            // Search for the product in the products list
            for (Product existingProduct : products) {
                if (existingProduct.name.equals(order.name)) {
                    // Product with the same name found in the products list
                    // Update quantity and price
                    existingProduct.quantity += order.quantity;
                    //existingProduct.price += order.price;
                    productFound = true;
                    break; // No need to continue searching once the product is found
                }
            }

            if (!productFound) {
                // Product not found in the products list, add it
                products.add(order);
            }

            // Update the inventory or perform other order processing tasks
            System.out.println("Order processed: " + order);
        }
    }
    // Method to record a sale and push it onto the stack
    public void recordSale(Product sale) {
        salesHistory.push(sale);
    }

    // Method to display the last N sales records
    public void displaySalesHistory(int n) {
        if(salesHistory.isEmpty()){
            System.out.println("No Sales Yet");
        }
        else {
            System.out.println("Sales History:");
            int count = 0;
            while (!salesHistory.isEmpty() && count < n) {
                System.out.println(salesHistory.pop());
                count++;
            }
        }
    }
    public void checkReorderPoint() {
        for (Product product : products) {
            if (product.quantity < REORDER_THRESHOLD) {
                int quantityToAdd = REORDER_THRESHOLD - product.quantity + 1;

                // Create a new Product for the reorder with the adjusted quantity
                Product reorderProduct = new Product(product.name, quantityToAdd, product.price);

                // Add the product to the reorder queue
                reorderQueue.add(reorderProduct);

                System.out.println("Reorder needed for: " + product.name + " (Quantity added: " + quantityToAdd + ")");
            }
        }
    }

    // Method to process reorder requests from the queue
    public void processReorders() {
        while (!reorderQueue.isEmpty()) {
            Product reorderProduct = reorderQueue.poll();
            // Implement reorder logic, e.g., notify the user or reorder automatically
            System.out.println("Reorder processed: " + reorderProduct);

            // Add the reordered product to the orderQueue
            addOrder(reorderProduct);
            System.out.println("Reorder added to orders: " + reorderProduct);
        }
    }

}
