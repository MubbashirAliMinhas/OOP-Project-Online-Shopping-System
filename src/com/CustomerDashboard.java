package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CustomerDashboard extends NavigationCustomer implements Initializable {

    @FXML private ScrollPane scrollPaneAll, scrollPane2, scrollPane3, scrollPane4;
    @FXML private FlowPane flowPaneAll, flowPane2, flowPane3, flowPane4;
    @FXML private Tab allTab, electronicsTab, clothingTab, cosmeticsTab;
    @FXML private Button searchButton, revertButton;
    @FXML private TextField searchField;

    private Stage window;
    public static Customer customer;
    public static CartDB cartDB;
    private String searchFilter = "";
    private final String ALL = "SELECT * FROM inventory WHERE (name LIKE '%";
    private final String ELECTRONICS = "SELECT * FROM inventory WHERE (category = 'Electronics' AND name LIKE '%";
    private final String CLOTHING = "SELECT * FROM inventory WHERE (category = 'Clothing' AND name LIKE '%";
    private final String COSMETICS = "SELECT * FROM inventory WHERE (category = 'Cosmetics' AND name LIKE '%";
    private Connection connection = DBConnection.getConnection();

    @FXML
    public void searchAction(ActionEvent event) {
        searchFilter = searchField.getText();
        displayProductsInCategories();
    }

    @FXML
    public void revertAction(ActionEvent event) {
        searchFilter = "";
        searchField.clear();
        displayProductsInCategories();
    }

    @FXML
    public void setCustomer(String name, String email, String password) {
        customer = new Customer(name, email, password);
    }

    @FXML
    public void initializeCart() {
        try {
            cartDB = (CartDB) ObjectHandler.readObject(connection, "user_cart", "cart", customer.email);

            for (int id : cartDB.items.keySet()) {
                String query = "SELECT * FROM inventory WHERE id = " + id;
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                cartDB.quantityInCart.remove(0);
                cartDB.quantities.remove(0);
                cartDB.availability.remove(0);
                if (resultSet.next()) {
                    int quantity = resultSet.getInt("quantity");
                    if (quantity < cartDB.items.get(id).quantity || quantity == 0) {
                        cartDB.availability.get(id).set(1, false);
                    } else {
                        cartDB.availability.get(id).set(1, true);
                    }
                    cartDB.quantities.put(id, quantity);

                    double price = resultSet.getDouble("price");
                    double discount = resultSet.getDouble("discount");
                    String name = resultSet.getString("name");
                    String category = resultSet.getString("category");
                    String imagePath = resultSet.getString("image_path");
                    if (discount == 0) {
                        cartDB.items.get(id).price = price;
                        cartDB.items.get(id).oldPrice = 0;
                    } else {
                        cartDB.items.get(id).oldPrice = price;
                        cartDB.items.get(id).price = price - (price * (discount/100));
                    }
                    cartDB.items.get(id).discount = discount;
                    cartDB.items.get(id).name = name;
                    cartDB.items.get(id).category = category;
                    cartDB.items.get(id).imagePath = imagePath;
                } else {
                    cartDB.availability.get(id).set(0, false);
                    cartDB.availability.get(id).set(1, false);
                }
                resultSet.close();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public VBox itemRenderer(int id, String imagePath, String name, double price, int quantity, String category, double discount) {
        VBox vbox1 = new VBox();
        VBox vbox2 = new VBox();
        vbox1.getStyleClass().add("vbox-inventory-main");
        vbox2.getStyleClass().add("vbox-inventory-details");

        Rectangle rectangle = new Rectangle(0, 0, 110, 110);
        rectangle.setArcHeight(10);
        rectangle.setArcWidth(10);

        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource(imagePath).toExternalForm()));
        rectangle.setFill(imagePattern);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("label-inventory-name");
        if (discount != 0) {
            double newPrice = price - (price * (discount / 100));
            Label priceLabel = new Label("Rs. " + newPrice);
            Label oldPriceLabel = new Label("Rs. " + price);
            Label discountLabel = new Label("-" + discount + "%");
            priceLabel.getStyleClass().add("label-inventory-price");
            oldPriceLabel.getStyleClass().add("label-inventory-price-old");
            discountLabel.getStyleClass().add("label-inventory-discount");
            HBox hBox = new HBox();
            hBox.getStyleClass().add("hbox-inventory-discount");
            hBox.getChildren().addAll(oldPriceLabel, discountLabel);
            vbox2.getChildren().addAll(nameLabel, priceLabel, hBox);
        } else {
            Label priceLabel = new Label("Rs. " + price);
            priceLabel.getStyleClass().add("label-inventory-price");
            vbox2.getChildren().addAll(nameLabel, priceLabel);
        }

        vbox1.getChildren().addAll(rectangle, vbox2);

        vbox1.setOnMousePressed(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("views/viewProduct.fxml"));
                Parent root = loader.load();
                ViewProductController viewProductController = loader.getController();
                viewProductController.displayProduct(id, imagePath, name, price, quantity, category, discount);
                vbox1.getScene().setRoot(root);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        return vbox1;
    }

    @FXML
    public void displayProductsInCategories() {
        if (allTab.isSelected()) {
            displayProducts(flowPaneAll, ALL);
        } else if (electronicsTab.isSelected()) {
            displayProducts(flowPane2, ELECTRONICS);
        } else if (clothingTab.isSelected()) {
            displayProducts(flowPane3, CLOTHING);
        } else if (clothingTab.isSelected()) {
            displayProducts(flowPane4, COSMETICS);
        }

        allTab.setOnSelectionChanged(e -> {
            displayProducts(flowPaneAll, ALL);
        });

        electronicsTab.setOnSelectionChanged(e -> {
            displayProducts(flowPane2, ELECTRONICS);
        });

        clothingTab.setOnSelectionChanged(e -> {
            displayProducts(flowPane3, CLOTHING);
        });

        cosmeticsTab.setOnSelectionChanged(e -> {
            displayProducts(flowPane4, COSMETICS);
        });
    }

    @FXML
    public void displayProducts(FlowPane flowPane, String tabFilter) {
        try {
            String query = tabFilter + searchFilter + "%')";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            flowPane.getChildren().clear();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String imagePath = resultSet.getString(2);
                String name = resultSet.getString(3);
                double price = resultSet.getDouble(4);
                int quantity = resultSet.getInt(5);
                String category = resultSet.getString(6);
                double discount = resultSet.getDouble(7);
                VBox vbox = itemRenderer(id, imagePath, name, price, quantity, category, discount);
                flowPane.getChildren().add(vbox);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scrollPaneAll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane3.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane4.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        displayProductsInCategories();
    }
}
