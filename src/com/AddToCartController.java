package com;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class AddToCartController implements Initializable {
    @FXML private Button cancelButton, incButton, decButton;
    @FXML private Label productQuantityInCart, nameLabel, priceLabel, quantityMessageLabel, oldPriceLabel, discountLabel;
    @FXML private TextField incField;
    @FXML private HBox discountHBox;
    @FXML private Rectangle imageView;
    @FXML private Button proceedButton;

    private String imagePath;
    private String name;
    private double price;
    private int quantity;
    private String category;
    private double discount;
    private double oldPrice = 0;
    private boolean updateMood = false;
    private int incValue;
    private int id;
    private int index;
    private HashMap<Integer, ProductAndCodesSelection> selectedItems;
    private Scene cartScene;
    private CartDB cartDB = CustomerDashboard.cartDB;

    @FXML
    public void setQuantities(int id, String imagePath, String name, double price, int quantity, String category, double discount) {
        this.id = id;
        this.imagePath = imagePath;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.discount = discount;
        
        if (discount != 0) {
            this.price = price - (price * (discount/100));
            this.oldPrice = price;
            priceLabel.setText("Rs. " + this.price);
            discountHBox.setManaged(true);
            discountHBox.setVisible(true);
            oldPriceLabel.setText("Rs. " + price);
            discountLabel.setText("-" + discount + "%");
        } else {
            this.price = price;
            priceLabel.setText("Rs. " + this.price);
            discountHBox.setManaged(false);
        }
        productQuantityInCart.setText(String.valueOf(cartDB.quantityInCart.get(id)));

        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource(imagePath).toExternalForm()));
        imageView.setFill(imagePattern);
        nameLabel.setText(name);

        if (cartDB.quantityInCart.containsKey(id) && cartDB.quantityInCart.get(id).equals(quantity)) {
            incButton.setDisable(true);
            decButton.setDisable(true);
            proceedButton.setDisable(true);
        }

        if (!updateMood && cartDB.quantityInCart.containsKey(id)) {
            incValue = quantity - cartDB.quantityInCart.get(id);
        } else {
            incValue = quantity;
        }
    }

    @FXML
    public void cancelAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void updateCart(int id, int index, HashMap<Integer, ProductAndCodesSelection> selectedItems, Scene cartScene) {
        this.id = id;
        this.index = index;
        this.selectedItems = selectedItems;
        this.cartScene = cartScene;
        quantity = cartDB.quantities.get(id);

        if (quantity < cartDB.items.get(id).quantity) {
            incField.setText(String.valueOf(quantity));
        } else {
            incField.setText(String.valueOf(cartDB.items.get(id).quantity));
        }

        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource(cartDB.items.get(id).imagePath).toExternalForm()));
        imageView.setFill(imagePattern);
        nameLabel.setText(cartDB.items.get(id).name);

        double discount = cartDB.items.get(id).discount;
        if (discount != 0) {
            priceLabel.setText("Rs. " + cartDB.items.get(id).price);
            discountHBox.setManaged(true);
            discountHBox.setVisible(true);
            oldPriceLabel.setText("Rs. " + cartDB.items.get(id).oldPrice);
            discountLabel.setText("-" + discount + "%");
        } else {
            priceLabel.setText("Rs" + cartDB.items.get(id).price);
            discountHBox.setManaged(false);
        }
        proceedButton.setText("UPDATE");
        productQuantityInCart.setVisible(false);
        quantityMessageLabel.setVisible(false);
        updateMood = true;

        incValue = quantity;
    }

    @FXML
    public void incAction() {
        incFieldHandler(() -> {
            if (!incField.getText().equals(String.valueOf(incValue))) {
                int value = Integer.parseInt(incField.getText());
                incField.setText(String.valueOf(++value));
            }
        });
    }

    @FXML
    public void decAction() {
        incFieldHandler(() -> {
            if (!incField.getText().equals("1")) {
                int value = Integer.parseInt(incField.getText());
                incField.setText(String.valueOf(--value));
            }
        });
    }

    @FXML
    public void proceedAction(ActionEvent event) {
        incFieldHandler(() -> {
            Item item = new Item(id, imagePath, name, price, Integer.parseInt(incField.getText()), category, discount, oldPrice);
            if (!updateMood && cartDB.quantityInCart.containsKey(id)) {
                cartDB.quantityInCart.put(id, cartDB.quantityInCart.get(id) + Integer.parseInt(incField.getText()));
                item.quantity = cartDB.quantityInCart.get(id);
            } else {
                cartDB.quantityInCart.put(id, Integer.parseInt(incField.getText()));
                cartDB.quantities.put(id, quantity);
                ArrayList<Boolean> availabilityList = new ArrayList<>();
                availabilityList.add(true);
                availabilityList.add(true);
                cartDB.availability.put(id, availabilityList);
            }
            if (!updateMood) {
                cartDB.items.put(id, item);
                System.out.println("Product is added to cart!!");
            } else {
                cartDB.items.get(id).quantity = Integer.parseInt(incField.getText());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("views/cart.fxml"));
                Parent root = loader.load();
                CartController cartController = loader.getController();
                cartController.preservePreviousState(index, selectedItems);
                cartScene.setRoot(root);
                System.out.println("Product is updated!!");
            }
            Stage stage = (Stage) proceedButton.getScene().getWindow();
            stage.close();
        });
    }

    @FXML
    public void incFieldHandler(IncFieldHandler handler) {
        try {
            if (Integer.parseInt(incField.getText()) > incValue) {
                incField.setText(String.valueOf(incValue));
            } else if (Integer.parseInt(incField.getText()) < 1) {
                incField.setText("1");
            } else {
                handler.handlerAction();
            }
        } catch (NumberFormatException e) {
            incField.setText("1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

interface IncFieldHandler {
    void handlerAction() throws IOException;
}