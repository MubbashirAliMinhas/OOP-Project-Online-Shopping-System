package com;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Orders implements Serializable {
    public ArrayList<Cart> carts = new ArrayList<>();
}

class Cart implements Serializable {
    public ArrayList<Item> items = new ArrayList<>();
    public String totalAmount;
    public String paymentMethod;
    public long date;
}

class CartDB implements Serializable {
    public HashMap<Integer, Item> items = new HashMap<>();
    public HashMap<Integer, Integer> quantityInCart = new HashMap<>();
    public HashMap<Integer, Integer> quantities = new HashMap<>();
    public HashMap<Integer, ArrayList<Boolean>> availability = new HashMap<>();
}

class Item implements Serializable {

    public int id;
    public String imagePath;
    public String name;
    public double price;
    public int quantity;
    public String category;
    public double discount;
    public double oldPrice;

    public Item(int id, String imagePath, String name, double price, int quantity, String category, double discount, double oldPrice) {
        this.id = id;
        this.imagePath = imagePath;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.discount = discount;
        this.oldPrice = oldPrice;
    }
}

