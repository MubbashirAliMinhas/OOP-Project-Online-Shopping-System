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

public class NavigationCustomer extends NavigationCloseAction {

    @FXML private Button logOutButton, cartButton, orderHistoryButton, accountButton, walletButton, homeButton;

    @FXML
    public void switchToCustomerDashboard() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/customerDashboard.fxml"));
        homeButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToLogIn(ActionEvent event) throws IOException {
        ObjectHandler.updateCart();
        CustomerDashboard.customer = null;
        CustomerDashboard.cartDB = null;
        super.switchToLogIn(event);
    }

    @FXML
    public void switchToCart() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/cart.fxml"));
        cartButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToOrderHistory() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/ordersHistory.fxml"));
        orderHistoryButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToAccount() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/account.fxml"));
        accountButton.getScene().setRoot(root);
    }

    @FXML
    public void switchToWallet() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/wallet.fxml"));
        walletButton.getScene().setRoot(root);
    }
}
