package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
import java.util.ResourceBundle;

public class InventoryController extends NavigationAdminInventory implements Initializable {

    @FXML private ScrollPane scrollPaneAll, scrollPane2, scrollPane3, scrollPane4;
    @FXML private FlowPane flowPaneAll, flowPane2, flowPane3, flowPane4;
    @FXML private Tab allTab, electronicsTab, clothingTab, cosmeticsTab;

    private final String ALL = "SELECT * FROM inventory";
    private final String ELECTRONICS = "SELECT * FROM inventory WHERE category = 'Electronics'";
    private final String CLOTHING = "SELECT * FROM inventory WHERE category = 'Clothing'";
    private final String COSMETICS = "SELECT * FROM inventory WHERE category = 'Cosmetics'";
    private Connection connection = DBConnection.getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scrollPaneAll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane3.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane4.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        if (allTab.isSelected()) {
            displayProducts(flowPaneAll, ALL);
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

    public VBox itemRenderer(int id, String imagePath, String name, double price, int quantity, String category, double discount) {
        VBox vbox1 = new VBox();
        VBox vbox2 = new VBox();
        vbox1.getStyleClass().add("vbox-inventory-main");
        vbox1.setStyle("-fx-cursor: none");
        vbox2.getStyleClass().add("vbox-inventory-details");

        Rectangle imageView = new Rectangle(0, 0, 110, 110);
        imageView.setArcHeight(10);
        imageView.setArcWidth(10);

        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource(imagePath).toExternalForm()));
        imageView.setFill(imagePattern);

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

        vbox1.getChildren().addAll(imageView, vbox2);
        return vbox1;
    }

    @FXML
    public void displayProducts(FlowPane flowPane, String tabFilter) {
        try {
            String query = tabFilter;
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

    public void fixBlur(ScrollPane scrollPane) {
        StackPane stackPane = (StackPane) scrollPane.lookup("ScrollPane .viewport");
        stackPane.setCache(false);
    }
}