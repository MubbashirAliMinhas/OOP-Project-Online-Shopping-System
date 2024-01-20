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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class RemoveItemController extends NavigationAdminInventory implements Initializable {

    @FXML private Button removeButton;
    @FXML private TableView<ProductAndCodesSelection> tableView;
    @FXML private TableColumn<ProductAndCodesSelection, CheckBox> checkBoxes;
    @FXML private TableColumn<ProductAndCodesSelection, ToggleButton> products;

    private ObservableList<ProductAndCodesSelection> observableList = FXCollections.observableArrayList();
    private ToggleGroup toggleGroup = new ToggleGroup();
    private Connection connection = DBConnection.getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String query = "SELECT id, image_path, name, price, quantity, category, discount FROM inventory";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CheckBox checkBox = new CheckBox("");
                String id = String.valueOf(resultSet.getInt(1));
                String imagePath = resultSet.getString(2);
                String name = resultSet.getString(3);
                double price = resultSet.getDouble(4);
                String quantity = String.valueOf(resultSet.getInt(5));
                String category = resultSet.getString(6);
                double discount = resultSet.getDouble(7);
                observableList.add(new ProductAndCodesSelection(checkBox, rowRenderer(id, imagePath, name, price, quantity, category, discount)));
            }
            checkBoxes.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
            products.setCellValueFactory(new PropertyValueFactory<>("toggleButton"));
            tableView.setItems(observableList);
            tableView.setSelectionModel(null);
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
        idLabel.getStyleClass().add("label-product-id-3");
        nameLabel.getStyleClass().add("label-product-name-3");
        quantityLabel.getStyleClass().add("label-product-quantity-3");
        categoryLabel.getStyleClass().add("label-product-category-3");

        if (discount != 0) {
            VBox vBoxPrice = new VBox();
            vBoxPrice.getStyleClass().add("vbox-product-price");
            double newPrice = price - (price * (discount/100));
            Label priceLabel = new Label("Rs. " + newPrice);
            Label oldPriceLabel = new Label("" + price);
            Label discountLabel = new Label("-" + discount + "%");
            priceLabel.getStyleClass().add("label-product-price-3");
            oldPriceLabel.getStyleClass().add("label-product-price-old");
            discountLabel.getStyleClass().add("label-product-discount");
            HBox hBoxDiscount = new HBox();
            hBoxDiscount.getStyleClass().add("hbox-product-discount");
            hBoxDiscount.getChildren().addAll(oldPriceLabel, discountLabel);
            vBoxPrice.getChildren().addAll(priceLabel, hBoxDiscount);
            hBox.getChildren().addAll(idLabel, imageView, nameLabel, vBoxPrice, quantityLabel, categoryLabel);
        } else {
            Label priceLabel = new Label("Rs. " + price);
            priceLabel.getStyleClass().add("label-product-price-3");
            hBox.getChildren().addAll(idLabel, imageView, nameLabel, priceLabel, quantityLabel, categoryLabel);
        }

        toggleButton.graphicProperty().setValue(hBox);
        toggleButton.setToggleGroup(toggleGroup);
        return toggleButton;
    }

    @FXML
    public void removeAction(ActionEvent event) throws SQLException {
        String query1 = "DELETE FROM inventory WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query1);

        ObservableList<ProductAndCodesSelection> removeItems = FXCollections.observableArrayList();

        int x = 0;
        for (ProductAndCodesSelection product : observableList) {
            if (product.getCheckBox().isSelected()) {
                HBox hBox = (HBox) observableList.get(x).getToggleButton().graphicProperty().getValue();
                Label label = (Label) hBox.getChildren().get(0);
                int id = Integer.parseInt(label.getText());
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                removeItems.add(product);
            }
            x++;
        }

        preparedStatement.close();
        tableView.getItems().removeAll(removeItems);
    }

}
