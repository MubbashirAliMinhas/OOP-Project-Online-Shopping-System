package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationAdmin extends NavigationCloseAction {

    @FXML private Button inventoryButton, discountCodesButton, logOutButton, salesButton, closeButton;

    @FXML
    public void switchToInventory(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/inventory.fxml"));
        inventoryButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToDiscountCodes(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/discountCodes.fxml"));
        discountCodesButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToSales(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/sales.fxml"));
        salesButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToLogIn(ActionEvent event) throws IOException {
        super.switchToLogIn(event);
    }
}
