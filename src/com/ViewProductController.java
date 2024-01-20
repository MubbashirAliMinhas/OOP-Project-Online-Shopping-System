package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.ResourceBundle;

public class ViewProductController extends NavigationCustomer implements Initializable {

    @FXML private Button goBackButton;
    @FXML private HBox hBoxDiscount;
    @FXML private Label nameLabel, quantityLabel, priceLabel, oldPriceLabel, discountLabel;
    @FXML private Rectangle imageView;
    @FXML public Button addToCartButton;

    private int id;
    private String imagePath;
    private String name;
    private double price;
    private int quantity;
    private String category;
    private double discount;

    @FXML
    public void switchToCustomerDashboard() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/customerDashboard.fxml"));
        goBackButton.getScene().setRoot(root);
    }

    public void itemRenderer(String imagePath, String name, double price, int quantity, double discount) {
        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource(imagePath).toExternalForm()));
        imageView.setFill(imagePattern);
        nameLabel.setText(name);
        quantityLabel.setText("Quantity: " + quantity);
        if (discount != 0) {
            hBoxDiscount.setVisible(true);
            oldPriceLabel.setText("Rs. " + price);
            discountLabel.setText("-" + discount + "%");
            priceLabel.setText("Rs. " + (price - (price * (discount/100))));
        } else {
            priceLabel.setText("Rs. " + price);
        }
    }

    @FXML
    public void switchToCustomerDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/customerDashboard.fxml"));
        goBackButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToAddToCart(ActionEvent event) throws IOException {
        AddUpdateCartAlert.proceed(id, imagePath, name, price, quantity, category, discount);
    }

    @FXML
    public void displayProduct(int id, String imagePath, String name, double price, int quantity, String category, double discount) {
        this.id = id;
        this.imagePath = imagePath;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.discount = discount;

        itemRenderer(imagePath, name, price, quantity, discount);
        if (quantity == 0) {
            addToCartButton.setDisable(true);
        }

        if (CustomerDashboard.cartDB.quantityInCart.containsKey(id)) {
            if (quantity - CustomerDashboard.cartDB.quantityInCart.get(id) <= 0) {
                addToCartButton.setDisable(true);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
