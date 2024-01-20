package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AccountController extends NavigationCustomer implements Initializable {

    @FXML private Button updateButton;
    @FXML private TextField nameField;
    @FXML private PasswordField currentPasswordField, newPasswordField, reEnterPasswordField;
    @FXML private Label validationLabel;

    private Connection connection = DBConnection.getConnection();

    @FXML
    public void updateAction(ActionEvent event) throws SQLException {
        if (!newPasswordField.getText().equals("") || !reEnterPasswordField.getText().equals("")) {
            if (nameField.getText().equals("") || currentPasswordField.getText().equals("") || newPasswordField.getText().equals("") || reEnterPasswordField.getText().equals("")) {
                validationLabel.setVisible(true);
                validationLabel.setText("Please enter all the fields");
            } else if (!CustomerDashboard.customer.password.equals(HashPassword.hash(currentPasswordField.getText()))) {
                validationLabel.setVisible(true);
                validationLabel.setText("Previous password is incorrect");
            } else if (!newPasswordField.getText().equals(reEnterPasswordField.getText())) {
                validationLabel.setVisible(true);
                validationLabel.setText("New password does not match");
            } else {
                String query = "UPDATE users SET name = ?, password = ? WHERE email = '" + CustomerDashboard.customer.email + "'";
                CustomerDashboard.customer.name = nameField.getText();
                CustomerDashboard.customer.setPassword(newPasswordField.getText());
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, CustomerDashboard.customer.name);
                preparedStatement.setString(2, CustomerDashboard.customer.password);
                preparedStatement.execute();
                preparedStatement.close();
                connection.close();

                if (nameField.getText().equals(CustomerDashboard.customer.name)) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Password successfully updated");
                } else {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Name and password successfully updated");
                }
            }
        } else {
            if (nameField.getText().equals("") || currentPasswordField.getText().equals("")) {
                validationLabel.setVisible(true);
                validationLabel.setText("Please enter all the fields");
            } else if (!CustomerDashboard.customer.password.equals(HashPassword.hash(currentPasswordField.getText()))) {
                validationLabel.setVisible(true);
                validationLabel.setText("Password is incorrect");
            } else {
                String query = "UPDATE users SET name = ? WHERE email = '" + CustomerDashboard.customer.email + "'";
                CustomerDashboard.customer.name = nameField.getText();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, CustomerDashboard.customer.name);
                preparedStatement.execute();
                preparedStatement.close();

                validationLabel.setVisible(true);
                validationLabel.setText("Name successfully updated");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameField.setText(CustomerDashboard.customer.name);
    }
}
