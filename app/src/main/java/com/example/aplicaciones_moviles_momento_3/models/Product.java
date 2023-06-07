package com.example.aplicaciones_moviles_momento_3.models;

public class Product {
    private int id;
    private int reference;
    private String description;
    private double cost;
    private int stock;

    public Product() {}

    public Product(int reference, String description, double cost, int stock) {
        this.reference = reference;
        this.description = description;
        this.cost = cost;
        this.stock = stock;
    }

    public Product(int id, int reference, String description, double cost, int stock) {
        this.id = id;
        this.reference = reference;
        this.description = description;
        this.cost = cost;
        this.stock = stock;
    }

    public int getReference() {
        return reference;
    }

    public String getDescription() {
        return description;
    }

    public double getCost() {
        return cost;
    }

    public int getStock() {
        return stock;
    }

    public int getId() {
        return id;
    }

    public double getIVA() {
        return getCost() * 0.19;
    }
}
