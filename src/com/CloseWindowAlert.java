package com;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class CloseWindowAlert {

    private double xOffset;
    private double yOffset;

    public void closeWindow(Stage stage) {
        try {
            Stage closeWindowStage = new Stage();
            closeWindowStage.initModality(Modality.APPLICATION_MODAL);
            closeWindowStage.initStyle(StageStyle.TRANSPARENT);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/closeWindow.fxml"));
            Parent root = loader.load();

            CloseWindowController closeWindowController = loader.getController();

            if (LoginController.loginCloseMode) {
                root.setStyle("-fx-background-color: black");
                closeWindowController.closeLabel.setStyle("-fx-text-fill: white");
            }

            closeWindowController.closeButton.setOnAction(e -> {
                if (CustomerDashboard.customer != null) {
                    ObjectHandler.updateCart();
                }
                DBConnection.closeConnection();
                stage.close();
                closeWindowController.closeAction(e);
            });

            Scene scene = new Scene(root, 400, 200);
            scene.setFill(Color.TRANSPARENT);

            scene.setOnMousePressed(event -> {
                xOffset = closeWindowStage.getX() - event.getScreenX();
                yOffset = closeWindowStage.getY() - event.getScreenY();
            });
            scene.setOnMouseDragged(event -> {
                closeWindowStage.setX(event.getScreenX() + xOffset);
                closeWindowStage.setY(event.getScreenY() + yOffset);
            });
            closeWindowStage.setScene(scene);
            closeWindowStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(Stage stage) {
        CloseWindowAlert closeWindowAlert = new CloseWindowAlert();
        closeWindowAlert.closeWindow(stage);
    }
}
