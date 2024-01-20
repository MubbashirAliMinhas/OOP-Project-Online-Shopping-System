package com;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectHandler {

    public static Object readObject(String tableName, String tableColumn, String email) {
        Connection connection = DBConnection.getConnection();
        return readObject(connection, tableName, tableColumn, email);
    }

    public static Object readObject(Connection connection, String tableName, String tableColumn, String email) {
        Object dataObject = null;
        try {
            String query = "SELECT " + tableColumn + " FROM " + tableName + " WHERE email = '" + email + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            byte[] bytes = resultSet.getBytes(1);
            resultSet.close();
            preparedStatement.close();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            dataObject = objectInputStream.readObject();
            byteArrayInputStream.close();
            objectInputStream.close();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dataObject;
    }

    public static void insertObject(Connection connection, String tableName, String tableColumn, String email, Object dataObject) {
        byte[] bytes = objectToByteArray(dataObject);
        String query = "INSERT INTO " + tableName + " (email, " + tableColumn + ") VALUES(?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            preparedStatement.setString(1, email);
            preparedStatement.setBytes(2, bytes);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateOrders(Connection connection, String email, Object dataObject) {
        byte[] bytes = objectToByteArray(dataObject);
        String query = "UPDATE user_orders SET orders = ? WHERE email = '" + email + "'";
        writeObject(connection, query, bytes);
    }

    public static void updateCart() {
        Connection connection = DBConnection.getConnection();
        updateCart(connection);
    }

    public static void updateCart(Connection connection) {
        byte[] bytes = objectToByteArray(CustomerDashboard.cartDB);
        String query = "UPDATE user_cart SET cart = ? WHERE email = '" + CustomerDashboard.customer.email + "'";
        writeObject(connection, query, bytes);
    }

    private static byte[] objectToByteArray(Object dataObject) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(dataObject);
            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private static void writeObject(Connection connection, String query, byte[] bytes) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            preparedStatement.setBytes(1, bytes);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}