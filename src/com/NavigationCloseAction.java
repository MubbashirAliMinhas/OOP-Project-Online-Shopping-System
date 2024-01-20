package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationCloseAction {
    @FXML private Button closeButton, logOutButton;

    @FXML
    public void closeAction(ActionEvent event) {
        CloseWindowAlert.close((Stage) closeButton.getScene().getWindow());
    }

    @FXML
    public void switchToLogIn(ActionEvent event) throws IOException {
        LoginController.loginCloseMode = true;
        Parent root = FXMLLoader.load(getClass().getResource("views/login.fxml"));
        Stage stage = (Stage) logOutButton.getScene().getWindow();
        Rectangle2D primaryBounds = Screen.getPrimary().getBounds();
        stage.setWidth(960);
        stage.setHeight(540);
        stage.setX((primaryBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primaryBounds.getHeight() - stage.getHeight()) / 2);
        logOutButton.getScene().setRoot(root);
    }
}
