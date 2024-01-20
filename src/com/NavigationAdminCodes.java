package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;

public class NavigationAdminCodes extends NavigationAdmin {
    @FXML private Button addDiscountCodeButton, removeDiscountCodeButton, updateDiscountCodeButton;

    @FXML
    public void switchToAddDiscountCode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/addDiscountCode.fxml"));
        addDiscountCodeButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToRemoveDiscountCode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/removeDiscountCode.fxml"));
        removeDiscountCodeButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToUpdateDiscountCode1(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/updateDiscountCode1.fxml"));
        updateDiscountCodeButton.getScene().setRoot(root);
    }
}
