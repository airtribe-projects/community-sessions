package com.airtribe.vendingmachine.inventory;

import com.airtribe.vendingmachine.model.Product;
import com.airtribe.vendingmachine.exception.InsufficientStockException;
import com.airtribe.vendingmachine.exception.ProductNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages inventory for the vending machine.
 * Follows SRP - Only responsible for inventory management.
 * Thread-safe implementation for concurrent access.
 */
public class Inventory {
    private final Map<String, Integer> productStock;
    private final Map<String, Product> products;

    public Inventory() {
        this.productStock = new HashMap<>();
        this.products = new HashMap<>();
    }

    /**
     * Adds a product to the inventory with initial stock.
     */
    public synchronized void addProduct(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        products.put(product.getCode(), product);
        productStock.put(product.getCode(), 
            productStock.getOrDefault(product.getCode(), 0) + quantity);
    }

    /**
     * Updates stock for an existing product.
     */
    public synchronized void updateStock(String productCode, int quantity) {
        if (!products.containsKey(productCode)) {
            throw new ProductNotFoundException("Product not found: " + productCode);
        }
        
        int currentStock = productStock.get(productCode);
        int newStock = currentStock + quantity;
        
        if (newStock < 0) {
            throw new IllegalArgumentException("Cannot reduce stock below 0");
        }
        
        productStock.put(productCode, newStock);
    }

    /**
     * Checks if a product is available in stock.
     */
    public synchronized boolean isAvailable(String productCode) {
        return products.containsKey(productCode) && 
               productStock.get(productCode) > 0;
    }

    /**
     * Gets the current stock quantity for a product.
     */
    public synchronized int getStock(String productCode) {
        if (!products.containsKey(productCode)) {
            throw new ProductNotFoundException("Product not found: " + productCode);
        }
        return productStock.get(productCode);
    }

    /**
     * Gets a product by its code.
     */
    public synchronized Product getProduct(String productCode) {
        Product product = products.get(productCode);
        if (product == null) {
            throw new ProductNotFoundException("Product not found: " + productCode);
        }
        return product;
    }

    /**
     * Dispenses a product by reducing its stock by 1.
     */
    public synchronized void dispenseProduct(String productCode) {
        if (!isAvailable(productCode)) {
            throw new InsufficientStockException("Product out of stock: " + productCode);
        }
        
        productStock.put(productCode, productStock.get(productCode) - 1);
    }

    /**
     * Gets all products in the inventory.
     */
    public synchronized Map<String, Product> getAllProducts() {
        return new HashMap<>(products);
    }

    /**
     * Gets stock information for all products.
     */
    public synchronized Map<String, Integer> getStockInfo() {
        return new HashMap<>(productStock);
    }
}
