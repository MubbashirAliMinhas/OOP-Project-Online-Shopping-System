package com;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Inventory {
    private Connection connection;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private ItemModel itemModel = new ItemModel();

    public void createConnection() {
        DBConnection dbConnection = new DBConnection();
        connection = dbConnection.startConnection();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void closeConnection() {
        try {
            preparedStatement.close();
            if (resultSet != null) {
                resultSet.close();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void nextResult() {
        try {
            resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean next() {
        boolean isAvailable = false;
        try {
            isAvailable = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isAvailable;
    }

    public void getAll() {
        try {
            String query = "SELECT id, image_path, name, price, quantity, category FROM inventory";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ItemModel getItem() {
        try {
            itemModel.setId(resultSet.getInt(1));
            itemModel.setImagePath(resultSet.getString(2));
            itemModel.setName(resultSet.getString(3));
            itemModel.setPrice(resultSet.getDouble(4));
            itemModel.setQuantity(resultSet.getInt(5));
            itemModel.setCategory(resultSet.getString(6));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemModel;
    }
}

class ItemModel {
    private int id;
    private String imagePath;
    private String name;
    private double price;
    private int quantity;
    private String category;

    public ItemModel() {}

    public ItemModel(int id, String imagePath, String name, double price, int quantity, String category) {
        this.id = id;
        this.imagePath = imagePath;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}