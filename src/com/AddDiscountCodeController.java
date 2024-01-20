package com;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddDiscountCodeController extends NavigationAdminCodes implements Initializable {

    @FXML private Button addButton;
    @FXML private TextField discountCodeField, amountField, discountField, quantityField;
    @FXML private Label validationLabel;
    @FXML private ComboBox<String> discountCodeTypeComboBox;

    private ObservableList<String> discountCodeTypes = FXCollections.observableArrayList("All users", "Per user");
    private Connection connection = DBConnection.getConnection();

    @FXML
    public void addAction(ActionEvent event) throws IOException, SQLException {
        if (discountCodeField.getText().equals("") || amountField.getText().equals("") || discountCodeField.getText().equals("") || quantityField.getText().equals("") || discountCodeTypeComboBox.getValue() == null) {
            validationLabel.setVisible(true);
            validationLabel.setText("Please enter all the required fields");
        } else {
            String query1 = "SELECT discount_code FROM codes WHERE discount_code = '" + discountCodeField.getText() + "'";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
            ResultSet resultSet1 = preparedStatement1.executeQuery();

            try {
                if (Double.parseDouble(amountField.getText()) < 0) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Please enter valid amount");
                } else if (Double.parseDouble(discountField.getText()) <= 0 || Double.parseDouble(discountField.getText()) > 100) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Please enter valid discount");
                } else if (Integer.parseInt(quantityField.getText()) < 0) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Please enter valid quantity");
                } else if (resultSet1.next()) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Discount code already exist in the database");
                    resultSet1.close();
                    preparedStatement1.close();
                } else {
                    String discountCode = discountCodeField.getText();
                    double amount = Double.parseDouble(amountField.getText());
                    double discount = Double.parseDouble(discountField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());
                    String type = discountCodeTypeComboBox.getValue();

                    String query2 = "INSERT INTO codes (discount_code, amount, discount, quantity, type) VALUES (?, ?, ?, ?, ?)";

                    PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                    preparedStatement2.setString(1, discountCode);
                    preparedStatement2.setDouble(2, amount);
                    preparedStatement2.setDouble(3, discount);
                    preparedStatement2.setInt(4, quantity);
                    preparedStatement2.setString(5, type);
                    preparedStatement2.execute();
                    preparedStatement2.close();

                    Parent root = FXMLLoader.load(getClass().getResource("views/discountCodes.fxml"));
                    addButton.getScene().setRoot(root);
                }
            } catch (NumberFormatException e) {
                validationLabel.setVisible(true);
                validationLabel.setText("Please enter valid data");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        discountCodeTypeComboBox.getItems().removeAll();
        discountCodeTypeComboBox.getItems().addAll(discountCodeTypes);
        discountCodeTypeComboBox.setOnAction(e -> {
            if (discountCodeTypeComboBox.getValue().equals("All users")) {
                System.out.println("How are ya");
            }
        });
    }
}
