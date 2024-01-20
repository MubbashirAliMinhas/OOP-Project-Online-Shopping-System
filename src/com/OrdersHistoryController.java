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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class OrdersHistoryController extends NavigationCustomer implements Initializable {

    @FXML private FlowPane flowPane;

    public HBox itemRenderer(int orderId, Cart cart) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox-orders-history");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        ImageView imageView = new ImageView(new Image(getClass().getResource("icons/order.png").toExternalForm()));
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);
        Label orderIdLabel = new Label(String.valueOf(orderId + 1));
        Label paymentMethodLabel = new Label(cart.paymentMethod);
        Label dateLabel = new Label(format.format(cart.date));
        orderIdLabel.getStyleClass().add("label-orders-history-order-id");
        paymentMethodLabel.getStyleClass().add("label-orders-history-payment-method");
        dateLabel.getStyleClass().add("label-orders-history-date");
        hBox.getChildren().addAll(imageView, orderIdLabel, paymentMethodLabel, dateLabel);
        hBox.setOnMousePressed(e -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/viewOrder.fxml"));
            try {
                Parent root = loader.load();
                ViewOrderController viewOrderController = loader.getController();
                viewOrderController.displayOrder(cart);
                hBox.getScene().setRoot(root);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        return hBox;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Orders orders = (Orders) ObjectHandler.readObject("user_orders", "orders", CustomerDashboard.customer.email);
        for (int x = 0; x < orders.carts.size(); x++) {
            flowPane.getChildren().add(itemRenderer(x, orders.carts.get(x)));
        }
    }
}
