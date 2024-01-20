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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DiscountCodesController extends NavigationAdminCodes implements Initializable {

    @FXML private TableView<ProductAndCodes> tableView;
    @FXML private TableColumn<ProductAndCodes, ToggleButton> discountCodes;

    private ObservableList<ProductAndCodes> observableList = FXCollections.observableArrayList();
    private ToggleGroup toggleGroup = new ToggleGroup();
    private Connection connection = DBConnection.getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String query = "SELECT id, discount_code, amount, discount, quantity, type FROM codes";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String id = String.valueOf(resultSet.getInt(1));
                String discountCode = resultSet.getString(2);
                String amount = String.valueOf(resultSet.getDouble(3));
                String discount = String.valueOf(resultSet.getDouble(4));
                String quantity = String.valueOf(resultSet.getInt(5));
                String type = resultSet.getString(6);
                observableList.add(new ProductAndCodes(rowRendererCode(id, discountCode, amount, discount, quantity, type)));
            }
            discountCodes.setCellValueFactory(new PropertyValueFactory<>("toggleButton"));
            tableView.setItems(observableList);
            tableView.setSelectionModel(null);
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ToggleButton rowRendererCode(String id, String discountCode, String amount, String discount, String quantity, String type) {
        ToggleButton toggleButton = new ToggleButton("");
        toggleButton.getStyleClass().add("button-item");

        HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox-code-1");

        ImageView imageView = new ImageView(new Image(getClass().getResource("icons/discount-code-2.png").toExternalForm()));
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);

        Label idLabel = new Label(id);
        Label discountCodeLabel = new Label(discountCode);
        Label amountLabel = new Label(amount);
        Label discountLabel = new Label(discount);
        Label quantityLabel = new Label(quantity);
        Label typeLabel = new Label(type);
        idLabel.getStyleClass().add("label-code-id-1");
        discountCodeLabel.getStyleClass().add("label-code-discount-code-1");
        amountLabel.getStyleClass().add("label-code-amount-1");
        discountLabel.getStyleClass().add("label-code-discount-1");
        quantityLabel.getStyleClass().add("label-code-quantity-1");
        typeLabel.getStyleClass().add("label-code-type-1");

        hBox.getChildren().addAll(idLabel, imageView, discountCodeLabel, amountLabel, discountLabel, quantityLabel, typeLabel);
        toggleButton.graphicProperty().setValue(hBox);
        toggleButton.setToggleGroup(toggleGroup);
        return toggleButton;
    }
}
