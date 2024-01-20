package com;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class User {
    private String name;
    protected String email;
    private String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void main(String[] args) {
        ArrayList<String> details = new ArrayList<>();
        details.add("Mubbashir Ali Minhas");
        details.add("12345");
        DB.addCustomer("mubbashir@gmail.com", details);
    }
}

class Customer {
    public String name;
    public String email;
    public String password;

    public Customer(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = HashPassword.hash(password);
    }

    public void setPassword(String password) {
        this.password = HashPassword.hash(password);
    }
}

class Admin extends User {
    public Admin(String name, String email, String password) {
        super(name, email, password);
    }
}

class DB {
    public static HashMap<String, ArrayList<String>> customers = new HashMap<>();
    public static HashMap<String, Orders> customersOrders = new HashMap<>();

    public static void addCustomer(String email, ArrayList<String> details) {
        customers.put(email, details);
    }

    public static void addCustomerOrder(String email, Orders orders) {
        customersOrders.put(email, orders);
    }

    public static Orders getCustomerOrder(String email) {
        return customersOrders.get(email);
    }

    public static String getName(String email) {
        return customers.get(email).get(0);
    }
}

/*

    public void logOutAction(ActionEvent event) throws IOException {
        customer = null;
        root = FXMLLoader.load(getClass().getResource("login.fxml"));
        scene = logOutButton.getScene();
        scene.setRoot(root);
    }

    public void signUpAction(ActionEvent event) {
        String name = nameFieldSignUp.getText();
        String email = emailFieldSignUp.getText();
        String password = passwordFieldSignUp.getText();
        ArrayList<String> details = new ArrayList<>();
        details.add(name);
        details.add(password);
        DB.addCustomer(email, details);
        ArrayList<Integer> visits = new ArrayList<>();
        visits.add(0);
        DB.addCustomerAction(email, visits);
        System.out.println("Customer details stored in DB successfully!!!");
    }

 */