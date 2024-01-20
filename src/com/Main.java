package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private double xOffset, yOffset;

    @Override
    public void start(Stage primaryStage) throws Exception {
        DBConnection.startConnection();

        Parent root = FXMLLoader.load(getClass().getResource("views/login.fxml"));
        primaryStage.setTitle("Ecommerce");
        Scene scene = new Scene(root, 960, 540);
        scene.setFill(Color.TRANSPARENT);
        scene.setOnMousePressed(event -> {
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
        });

        scene.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
        });

        Screen.getPrimary().getOutputScaleX();
        Screen.getPrimary().getOutputScaleY();

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            CloseWindowAlert.close(primaryStage);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
