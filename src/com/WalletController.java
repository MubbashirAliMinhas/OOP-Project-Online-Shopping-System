package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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

public class WalletController extends NavigationCustomer implements Initializable {

    @FXML private Button depositButton, withdrawButton;
    @FXML private TextField amountField;
    @FXML private Label balanceLabel, validationLabel;

    private double balance;
    private Connection connection = DBConnection.getConnection();

    @FXML
    public void depositAction(ActionEvent event) throws SQLException {
        if (amountField.getText().equals("")) {
            validationLabel.setVisible(true);
            validationLabel.setText("Please enter deposit amount");
        } else {
            try {
                if (Double.parseDouble(amountField.getText()) < 0) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Enter valid deposit amount");
                } else {
                    balance += Double.parseDouble(amountField.getText());
                    String query = "UPDATE user_wallet SET balance = ? WHERE email = '" + CustomerDashboard.customer.email + "'";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setDouble(1, balance);
                    preparedStatement.execute();
                    preparedStatement.close();

                    balanceLabel.setText(String.valueOf(balance));
                    validationLabel.setVisible(true);
                    validationLabel.setText("Rs. " + amountField.getText() + " deposited in the wallet");
                    amountField.clear();
                }
            } catch (NumberFormatException e) {
                validationLabel.setVisible(true);
                validationLabel.setText("Enter valid amount");
            }
        }
    }

    @FXML
    public void withdrawAction(ActionEvent event) throws SQLException {
        if (amountField.getText().equals("")) {
            validationLabel.setVisible(true);
            validationLabel.setText("Please enter withdraw amount");
        } else {
            try {
                if (Double.parseDouble(amountField.getText()) < 0) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Enter valid withdraw amount");
                } else if (balance - Double.parseDouble(amountField.getText()) < 0) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Not enough balance in the wallet");
                } else {
                    balance -= Double.parseDouble(amountField.getText());
                    String query = "UPDATE user_wallet SET balance = ? WHERE email = '" + CustomerDashboard.customer.email + "'";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setDouble(1, balance);
                    preparedStatement.execute();
                    preparedStatement.close();

                    balanceLabel.setText(String.valueOf(balance));
                    validationLabel.setVisible(true);
                    validationLabel.setText("Rs. " + amountField.getText() + " withdrawn from the wallet");
                    amountField.clear();
                }
            } catch (NumberFormatException e) {
                validationLabel.setVisible(true);
                validationLabel.setText("Enter valid amount");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            String query = "SELECT balance FROM user_wallet WHERE email = '" + CustomerDashboard.customer.email +  "'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            balance = resultSet.getDouble(1);
            balanceLabel.setText(String.valueOf(balance));
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
