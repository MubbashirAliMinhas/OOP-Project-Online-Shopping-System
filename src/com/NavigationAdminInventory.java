package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;

public class NavigationAdminInventory extends NavigationAdmin {
    @FXML private Button addItemButton, removeItemButton, updateItemButton;

    @FXML
    public void addItemAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/addItem.fxml"));
        addItemButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToRemoveItem(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/removeItem.fxml"));
        removeItemButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToUpdateItem1(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/updateItem1.fxml"));
        updateItemButton.getScene().setRoot(root);
    }
}
