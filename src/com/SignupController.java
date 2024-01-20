package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class SignupController implements Initializable {

    private Stage window;
    private Connection connection = DBConnection.getConnection();

    @FXML private Button goBackButton, closeButton;
    @FXML private TextField nameFieldSignUp, emailFieldSignUp;
    @FXML private PasswordField passwordFieldSignUp, confirmPasswordField;
    @FXML private Label validationLabel;
    @FXML private Rectangle backgroundImage;

    @FXML
    public void switchToLogIn() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/login.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        String email = emailFieldSignUp.getText();
        loginController.setFields(email);
        goBackButton.getScene().setRoot(root);
    }


    @FXML
    public void closeAction(ActionEvent event) {
        CloseWindowAlert.close((Stage) closeButton.getScene().getWindow());
    }

    @FXML
    public void signUpAction() throws SQLException, IOException {
        String name = nameFieldSignUp.getText();
        String email = emailFieldSignUp.getText();
        String password = passwordFieldSignUp.getText();

        String emailValidator = "^\\w+@\\w+\\.$";
        Pattern pattern = Pattern.compile(emailValidator);

        String query1 = "SElECT email FROM users WHERE email = '" + email + "'";
        PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
        ResultSet resultSet = preparedStatement1.executeQuery();

        if (nameFieldSignUp.getText().equals("") || emailFieldSignUp.getText().equals("") || passwordFieldSignUp.getText().equals("") || confirmPasswordField.getText().equals("")) {
            validationLabel.setVisible(true);
            validationLabel.setText("Please fill all the fields");
        } else if (!passwordFieldSignUp.getText().equals(confirmPasswordField.getText())) {
            validationLabel.setVisible(true);
            validationLabel.setText("Password does not match");
        } else if (!resultSet.next()) {
            String query2 = "INSERT INTO users (name, email, password) VALUES(?, ?, ?)";

            PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
            preparedStatement2.setString(1, name);
            preparedStatement2.setString(2, email.toLowerCase(Locale.ROOT));
            preparedStatement2.setString(3, HashPassword.hash(password));
            preparedStatement2.execute();
            preparedStatement2.close();

            ObjectHandler.insertObject(connection, "user_orders", "orders", email, new Orders());

            String query4 = "INSERT INTO user_wallet (email, balance) VALUES(?, ?)";
            PreparedStatement preparedStatement4 = connection.prepareStatement(query4);
            preparedStatement4.setString(1, email);
            preparedStatement4.setDouble(2, 0);
            preparedStatement4.execute();
            preparedStatement4.close();

            ObjectHandler.insertObject(connection, "user_cart", "cart", email, new CartDB());

            System.out.println("Customer details stored in DB successfully!!!");
        } else {
            validationLabel.setVisible(true);
            validationLabel.setText("Account already exist with this email address");
        }
        resultSet.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource("backgrounds/login-form.jpg").toExternalForm()));
        backgroundImage.setFill(imagePattern);
    }
}