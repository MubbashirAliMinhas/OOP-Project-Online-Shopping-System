package com;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    private Stage window;
    private String adminEmail = "admin@store.com";
    private String adminPassword = "f2810f98c28d432a94c127ff3714960e37dbc1c15bb1dfd2fb947889c2d956ba";
    private Connection connection = DBConnection.getConnection();
    public static boolean loginCloseMode = true;

    @FXML private Button signUpButton, logInButton, closeButton;
    @FXML private TextField emailFieldLogIn;
    @FXML private PasswordField passwordFieldLogIn;
    @FXML private Label validationLabel;
    @FXML private Rectangle backgroundImage;

    @FXML
    public void switchToSignUp() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/signup.fxml"));
        signUpButton.getScene().setRoot(root);
    }

    @FXML
    public void closeAction(ActionEvent event) {
        CloseWindowAlert.close((Stage) closeButton.getScene().getWindow());
    }

    @FXML
    public void logInAction() throws IOException, SQLException {
        String query1 = "SELECT name, email, password FROM users WHERE email = '" + emailFieldLogIn.getText().toLowerCase(Locale.ROOT) + "'";
        PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
        ResultSet resultSet = preparedStatement1.executeQuery();

        if (emailFieldLogIn.getText().equals("") || passwordFieldLogIn.getText().equals("")) {
            validationLabel.setVisible(true);
            validationLabel.setText("Please enter all the fields");
        } else if (resultSet.next()) {
            String name = resultSet.getString(1);
            String email = resultSet.getString(2);
            String password = resultSet.getString(3);
            resultSet.close();
            preparedStatement1.close();

            if (emailFieldLogIn.getText().toLowerCase(Locale.ROOT).equals(adminEmail) && HashPassword.hash(passwordFieldLogIn.getText()).equals(adminPassword)) {
                Parent root = FXMLLoader.load(getClass().getResource("views/adminDashboard.fxml"));
                Stage stage = (Stage) logInButton.getScene().getWindow();
                resizeWindow(stage);
                loginCloseMode = false;
                logInButton.getScene().setRoot(root);
            } else if (password.equals(HashPassword.hash(passwordFieldLogIn.getText()))) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("views/customerDashboard.fxml"));
                Parent root = loader.load();
                CustomerDashboard customerDashboard = loader.getController();
                customerDashboard.setCustomer(name, email, passwordFieldLogIn.getText());
                customerDashboard.initializeCart();
                Stage stage = (Stage) logInButton.getScene().getWindow();
                resizeWindow(stage);
                loginCloseMode = false;
                logInButton.getScene().setRoot(root);
            } else {
                validationLabel.setVisible(true);
                validationLabel.setText("Incorrect password");
            }
        } else {
            validationLabel.setVisible(true);
            validationLabel.setText("Account does not exist");
        }
    }

    @FXML
    public void resizeWindow(Stage stage) {
        Rectangle2D primaryBounds = Screen.getPrimary().getBounds();
        stage.setWidth(1240);
        stage.setHeight(715);
        stage.setX((primaryBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primaryBounds.getHeight() - stage.getHeight()) / 2);
    }

    @FXML
    public void setFields(String email) {
        emailFieldLogIn.setText(email);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource("backgrounds/login-background.jpg").toExternalForm()));
        backgroundImage.setFill(imagePattern);

        //emailFieldLogIn.setText("mubbashir@gmail.com");
        //passwordFieldLogIn.setText("12345");
    }
}