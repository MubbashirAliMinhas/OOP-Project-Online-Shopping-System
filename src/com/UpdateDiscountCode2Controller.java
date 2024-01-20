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

public class UpdateDiscountCode2Controller extends NavigationAdminCodes implements Initializable {

    @FXML private Button goBackButton, updateButton;
    @FXML private TextField discountCodeField, amountField, discountField, quantityField;
    @FXML private Label validationLabel;
    @FXML private ComboBox<String> discountCodeTypeComboBox;

    private int id;
    private String discountCode, type;
    private ObservableList<String> discountCodeTypes = FXCollections.observableArrayList("All users", "Per user");
    private Connection connection = DBConnection.getConnection();

    @FXML
    public void setDiscountCode(int id) throws SQLException {
        this.id = id;
        String query = "SELECT discount_code, amount, discount, quantity, type FROM codes WHERE id = " + id;

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        discountCode = resultSet.getString(1);
        String amount = String.valueOf(resultSet.getDouble(2));
        String discount = String.valueOf(resultSet.getDouble(3));
        String quantity = String.valueOf(resultSet.getInt(4));
        type = resultSet.getString(5);
        discountCodeField.setText(discountCode);
        amountField.setText(amount);
        discountField.setText(discount);
        quantityField.setText(quantity);
        discountCodeTypeComboBox.getSelectionModel().select(type);
        resultSet.close();
        preparedStatement.close();
    }

    @FXML
    public void switchToUpdateDiscountCode1(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/updateDiscountCode1.fxml"));
        goBackButton.getScene().setRoot(root);
    }

    @FXML
    public void updateAction(ActionEvent event) throws IOException, SQLException {
        if (discountCodeField.getText().equals("") || amountField.getText().equals("") || discountCodeField.getText().equals("") || quantityField.getText().equals("")) {
            validationLabel.setVisible(true);
            validationLabel.setText("Please enter all the required fields");
        } else {
            String query1 = "SELECT discount_code FROM codes WHERE discount_code = '" + discountCodeField.getText() + "'";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
            ResultSet resultSet1 = preparedStatement1.executeQuery();

            try {
                if (type.equals("Per user")) {
                    String query = "DELETE FROM per_user_codes_quantity WHERE code_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, id);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                }

                if (Double.parseDouble(amountField.getText()) < 0) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Please enter valid amount");
                } else if (Double.parseDouble(discountField.getText()) <= 0 || Double.parseDouble(discountField.getText()) > 100) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Please enter valid discount");
                } else if (Integer.parseInt(quantityField.getText()) < 0) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Please enter valid quantity");
                } else if ((!discountCode.equals(discountCodeField.getText())) && resultSet1.next()) {
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

                    String query2 = "UPDATE codes SET discount_code = ?, amount = ?, discount = ?, quantity = ?, type = ? WHERE id = " + id;

                    PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                    preparedStatement2.setString(1, discountCode);
                    preparedStatement2.setDouble(2, amount);
                    preparedStatement2.setDouble(3, discount);
                    preparedStatement2.setInt(4, quantity);
                    preparedStatement2.setString(5, type);
                    preparedStatement2.execute();
                    preparedStatement2.close();

                    Parent root = FXMLLoader.load(getClass().getResource("views/updateDiscountCode1.fxml"));
                    updateButton.getScene().setRoot(root);
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
    }
}
