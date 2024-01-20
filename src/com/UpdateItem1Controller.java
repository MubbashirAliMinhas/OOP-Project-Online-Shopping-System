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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UpdateItem1Controller extends NavigationAdminInventory implements Initializable {

    @FXML private Button goBackButton, updateItemButton;
    @FXML private TableView<ProductAndCodes> tableView;
    @FXML private TableColumn<ProductAndCodes, ToggleButton> products;

    private ObservableList<ProductAndCodes> observableList = FXCollections.observableArrayList();
    private ToggleGroup toggleGroup = new ToggleGroup();
    private Connection connection = DBConnection.getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String query = "SELECT id, image_path, name, price, quantity, category, discount FROM inventory";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = String.valueOf(resultSet.getInt(1));
                String imagePath = resultSet.getString(2);
                String name = resultSet.getString(3);
                double price = resultSet.getDouble(4);
                String quantity = String.valueOf(resultSet.getInt(5));
                String category = resultSet.getString(6);
                double discount = resultSet.getDouble(7);
                observableList.add(new ProductAndCodes(rowRenderer(id, imagePath, name, price, quantity, category, discount)));
            }
            products.setCellValueFactory(new PropertyValueFactory<>("toggleButton"));
            tableView.setItems(observableList);
            tableView.setSelectionModel(null);
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToUpdate2(ActionEvent event) throws IOException, SQLException {
        boolean isSelected = false;
        int x = 0;
        int id = 0;
        for (ProductAndCodes productAndCodes : observableList) {
            if (productAndCodes.getToggleButton().isSelected()) {
                HBox hBox = (HBox) observableList.get(x).getToggleButton().graphicProperty().getValue();
                Label label = (Label) hBox.getChildren().get(0);
                id = Integer.parseInt(label.getText());
                isSelected = true;
                break;
            }
            x++;
        }
        if (isSelected) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/updateItem2.fxml"));
            Parent root = loader.load();
            UpdateItem2Controller updateItem2Controller = loader.getController();
            updateItem2Controller.setUpdateItem(id);
            updateItemButton.getScene().setRoot(root);
        }
    }

    public ToggleButton rowRenderer(String id, String imagePath, String name, double price, String quantity, String category, double discount) {
        ToggleButton toggleButton = new ToggleButton("");
        toggleButton.getStyleClass().add("button-item");

        HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox-product-1");

        ImageView imageView = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);

        Label idLabel = new Label(id);
        Label nameLabel = new Label(name);
        Label quantityLabel = new Label(quantity);
        Label categoryLabel = new Label(category);
        idLabel.getStyleClass().add("label-product-id-1");
        nameLabel.getStyleClass().add("label-product-name-1");
        quantityLabel.getStyleClass().add("label-product-quantity-1");
        categoryLabel.getStyleClass().add("label-product-category-1");

        if (discount != 0) {
            VBox vBoxPrice = new VBox();
            vBoxPrice.getStyleClass().add("vbox-product-price");
            double newPrice = price - (price * (discount/100));
            Label priceLabel = new Label("Rs. " + newPrice);
            Label oldPriceLabel = new Label("" + price);
            Label discountLabel = new Label("-" + discount + "%");
            priceLabel.getStyleClass().add("label-product-price-1");
            oldPriceLabel.getStyleClass().add("label-product-price-old");
            discountLabel.getStyleClass().add("label-product-discount");
            HBox hBoxDiscount = new HBox();
            hBoxDiscount.getStyleClass().add("hbox-product-discount");
            hBoxDiscount.getChildren().addAll(oldPriceLabel, discountLabel);
            vBoxPrice.getChildren().addAll(priceLabel, hBoxDiscount);
            hBox.getChildren().addAll(idLabel, imageView, nameLabel, vBoxPrice, quantityLabel, categoryLabel);
        } else {
            Label priceLabel = new Label("Rs. " + price);
            priceLabel.getStyleClass().add("label-product-price-1");
            hBox.getChildren().addAll(idLabel, imageView, nameLabel, priceLabel, quantityLabel, categoryLabel);
        }

        toggleButton.graphicProperty().setValue(hBox);
        toggleButton.setToggleGroup(toggleGroup);
        return toggleButton;
    }
}
