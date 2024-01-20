package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class SalesController extends NavigationAdmin implements Initializable {

    @FXML private FlowPane flowPane;

    private Connection connection = DBConnection.getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            String query1 = "SELECT order_id, email, payment_method, order_date FROM sales ORDER BY id DESC";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
            ResultSet resultSet1 = preparedStatement1.executeQuery();

            PreparedStatement preparedStatement2 = null;
            ResultSet resultSet2 = null;

            while (resultSet1.next()) {
                int orderId = resultSet1.getInt(1);
                String email = resultSet1.getString(2);
                String paymentMethod = resultSet1.getString(3);
                long date = resultSet1.getLong(4);

                String query2 = "SELECT name FROM users WHERE email = '" + email + "'";
                preparedStatement2 = connection.prepareStatement(query2);
                resultSet2 = preparedStatement2.executeQuery();
                resultSet2.next();

                String name = resultSet2.getString(1);
                flowPane.getChildren().add(salesRenderer(name, email, orderId, paymentMethod, date));
                resultSet2.close();
                preparedStatement2.close();
            }
            resultSet1.close();
            preparedStatement1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public HBox salesRenderer(String name, String email, int orderId, String paymentMethod, long date) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox-sales");

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        ImageView imageView = new ImageView(new Image(getClass().getResource("icons/order.png").toExternalForm()));
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        Label nameLabel = new Label(name);
        Label emailLabel = new Label(email);
        Label orderIdLabel = new Label(String.valueOf(orderId));
        Label paymentMethodLabel = new Label(paymentMethod);
        Label dateLabel = new Label(format.format(date));
        nameLabel.getStyleClass().add("label-sales-name");
        emailLabel.getStyleClass().add("label-sales-email");
        orderIdLabel.getStyleClass().add("label-sales-order-id");
        paymentMethodLabel.getStyleClass().add("label-sales-payment-method");
        dateLabel.getStyleClass().add("label-sales-date");

        hBox.getChildren().addAll(imageView, nameLabel, emailLabel, orderIdLabel, paymentMethodLabel, dateLabel);
        hBox.setOnMousePressed(ev -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("views/viewCustomerOrder.fxml"));
                Parent root = loader.load();
                ViewCustomerOrderController viewCustomerOrderController = loader.getController();
                viewCustomerOrderController.displayCustomerOrder(email, orderId);
                hBox.getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return hBox;
    }
}
