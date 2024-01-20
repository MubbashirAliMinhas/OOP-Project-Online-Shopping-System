package com;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AddUpdateCartAlert {

    private double xOffset;
    private double yOffset;

    public void addToCart(int id, String imagePath, String name, double price, int quantity, String category, double discount) {
        try {
            Stage cartStage = new Stage();
            cartStage.initModality(Modality.APPLICATION_MODAL);
            cartStage.initStyle(StageStyle.TRANSPARENT);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/addToCart.fxml"));
            Parent root = loader.load();
            AddToCartController addToCartController = loader.getController();
            addToCartController.setQuantities(id, imagePath, name, price, quantity, category, discount);
            Scene scene = new Scene(root, 600, 340);
            scene.setFill(Color.TRANSPARENT);

            scene.setOnMousePressed(event -> {
                xOffset = cartStage.getX() - event.getScreenX();
                yOffset = cartStage.getY() - event.getScreenY();
            });
            scene.setOnMouseDragged(event -> {
                cartStage.setX(event.getScreenX() + xOffset);
                cartStage.setY(event.getScreenY() + yOffset);
            });
            cartStage.setScene(scene);
            cartStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCart(int id, int index, HashMap<Integer, ProductAndCodesSelection> selectedItems, Scene cartScene) {
        try {
            Stage cartStage = new Stage();
            cartStage.initModality(Modality.APPLICATION_MODAL);
            cartStage.initStyle(StageStyle.TRANSPARENT);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/addToCart.fxml"));
            Parent root = loader.load();
            AddToCartController addToCartController = loader.getController();
            addToCartController.updateCart(id, index, selectedItems, cartScene);
            Scene scene = new Scene(root, 600, 340);
            scene.setFill(Color.TRANSPARENT);

            scene.setOnMousePressed(event -> {
                xOffset = cartStage.getX() - event.getScreenX();
                yOffset = cartStage.getY() - event.getScreenY();
            });
            scene.setOnMouseDragged(event -> {
                cartStage.setX(event.getScreenX() + xOffset);
                cartStage.setY(event.getScreenY() + yOffset);
            });
            cartStage.setScene(scene);
            cartStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void proceed(int id, String imagePath, String name, double price, int quantity, String category, double discount) {
        AddUpdateCartAlert addUpdateCartAlert = new AddUpdateCartAlert();
        addUpdateCartAlert.addToCart(id, imagePath, name, price, quantity, category, discount);
    }

    public static void update(int id, int index, HashMap<Integer, ProductAndCodesSelection> selectedItems, Scene cartScene) {
        AddUpdateCartAlert addUpdateCartAlert = new AddUpdateCartAlert();
        addUpdateCartAlert.updateCart(id, index, selectedItems, cartScene);
    }
}
