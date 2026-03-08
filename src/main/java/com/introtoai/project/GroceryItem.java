package com.introtoai.project;

public class GroceryItem {
    private String name;
    private double cost;
    private int calories;

    public GroceryItem(String name, double cost, int calories) {
        this.name = name;
        this.cost = cost;
        this.calories = calories;
    }

    // Getters
    public String getName() { return name; }
    public double getCost() { return cost; }
    public int getCalories() { return calories; }
}
