package com;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewCustomerOrderController extends NavigationAdmin implements Initializable {

    @FXML public Button goBackButton;
    @FXML private Label totalAmountLabel;
    @FXML private TableView<ProductAndCodes> tableView;
    @FXML private TableColumn<ProductAndCodes, ToggleButton> products;

    private ObservableList<ProductAndCodes> observableList = FXCollections.observableArrayList();
    private ToggleGroup toggleGroup = new ToggleGroup();

    public ToggleButton rowRenderer(String id, String imagePath, String name, double price, String quantity, String category) {
        ToggleButton toggleButton = new ToggleButton("");
        toggleButton.getStyleClass().add("button-item");

        HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox-product-2");

        Rectangle imageView = new Rectangle(0, 0, 40, 40);
        imageView.setArcWidth(10);
        imageView.setArcHeight(10);
        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource(imagePath).toExternalForm()));
        imageView.setFill(imagePattern);

        price *= Integer.parseInt(quantity);

        Label idLabel = new Label(id);
        Label nameLabel = new Label(name);
        Label priceLabel = new Label("Rs. " + price);
        Label quantityLabel = new Label(quantity);
        Label categoryLabel = new Label(category);
        idLabel.getStyleClass().add("label-product-id-1");
        nameLabel.getStyleClass().add("label-product-name-1");
        priceLabel.getStyleClass().add("label-product-price-1");
        quantityLabel.getStyleClass().add("label-product-quantity-1");
        categoryLabel.getStyleClass().add("label-product-category-1");

        hBox.getChildren().addAll(idLabel, imageView, nameLabel, priceLabel, quantityLabel, categoryLabel);
        toggleButton.graphicProperty().setValue(hBox);
        toggleButton.setToggleGroup(toggleGroup);
        return toggleButton;
    }
    
    @FXML
    public void displayCustomerOrder(String email, int orderId) {
        Orders orders = (Orders) ObjectHandler.readObject("user_orders", "orders", email);
        Cart cart = orders.carts.get(orderId - 1);
        for (Item item : cart.items) {
            String id = String.valueOf(item.id);
            String imagePath = item.imagePath;
            String name = item.name;
            double price = item.price;
            String quantity = String.valueOf(item.quantity);
            String category = item.category;
            observableList.add(new ProductAndCodes(rowRenderer(id, imagePath, name, price, quantity, category)));
        }
        products.setCellValueFactory(new PropertyValueFactory<>("toggleButton"));
        tableView.setItems(observableList);
        tableView.setSelectionModel(null);
        totalAmountLabel.setText("Rs. " + cart.totalAmount);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
