package com;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class UpdateItem2Controller extends NavigationAdminInventory implements Initializable {

    @FXML private Button browseButton, updateButton, goBackButton;
    @FXML private TextField nameField, quantityField, priceField, discountField;
    @FXML private Rectangle imageView;
    @FXML private ComboBox<String> productCategoryComboBox;
    @FXML private Label validationLabel;

    private int index;
    private FileChooser fileChooser;
    private String imagePath;
    private Stage window;
    private FileChooser.ExtensionFilter imageFormat = new FileChooser.ExtensionFilter("image file", "*.png", "*.jpg", "*.webp");
    private ObservableList<String> observableList = FXCollections.observableArrayList("Electronics", "Clothing", "Cosmetics");
    private Connection connection = DBConnection.getConnection();

    public void setUpdateItem(int index) throws SQLException {
        this.index = index;
        String query = "SELECT image_path, name, price, quantity, category, discount FROM inventory WHERE id = " + index;
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        imagePath = resultSet.getString(1);
        String name = resultSet.getString(2);
        String price = String.valueOf(resultSet.getDouble(3));
        String quantity = String.valueOf(resultSet.getInt(4));
        String category = resultSet.getString(5);
        String discount = String.valueOf(resultSet.getDouble(6));

        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource(imagePath).toExternalForm()));
        imageView.setFill(imagePattern);
        nameField.setText(name);
        priceField.setText(price);
        quantityField.setText(quantity);
        if (discount.equals("0.0")) {
            discountField.setText("");
        } else {
            discountField.setText(discount);
        }
        productCategoryComboBox.getSelectionModel().select(category);

        resultSet.close();
        preparedStatement.close();
    }

    @FXML
    public void switchToUpdateItem1(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/updateItem1.fxml"));
        goBackButton.getScene().setRoot(root);
    }

    @FXML
    public void browseAction(ActionEvent event) {
        window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File path = new File("").getAbsoluteFile();
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(imageFormat);
        fileChooser.setInitialDirectory(path);
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            imagePath = new File("src/com").toURI().relativize(file.toURI()).getPath();
            ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource(imagePath).toExternalForm()));
            imageView.setFill(imagePattern);
        }
    }

    @FXML
    public void updateAction(ActionEvent event) throws IOException, SQLException {
        if (imagePath == null || nameField.getText().equals("") || priceField.getText().equals("") || quantityField.getText().equals("") || productCategoryComboBox.getValue() == null) {
            validationLabel.setVisible(true);
            validationLabel.setText("Please add all the required information");
        } else {
            try {
                if (Double.parseDouble(priceField.getText()) < 0) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Please enter valid price.");
                } else if (Integer.parseInt(quantityField.getText()) < 0) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Please enter valid quantity");
                } else if (!discountField.getText().equals("") && (Double.parseDouble(discountField.getText()) <= 0 || Double.parseDouble(discountField.getText()) > 100)) {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Please enter valid discount");
                }  else {
                    String query = "UPDATE inventory SET image_path = ?, name = ?, price = ?, quantity = ?, category = ?, discount = ? WHERE id = " + index;
                    PreparedStatement preparedStatement = connection.prepareStatement(query);

                    preparedStatement.setString(1, imagePath);
                    preparedStatement.setString(2, nameField.getText());
                    preparedStatement.setDouble(3, Double.parseDouble(priceField.getText()));
                    preparedStatement.setInt(4, Integer.parseInt(quantityField.getText()));
                    preparedStatement.setString(5, productCategoryComboBox.getValue());
                    if (discountField.getText().equals("")) {
                        preparedStatement.setNull(6, Types.NULL);
                    } else {
                        preparedStatement.setDouble(6, Double.parseDouble(discountField.getText()));
                    }
                    preparedStatement.execute();
                    preparedStatement.close();

                    Parent root = FXMLLoader.load(getClass().getResource("views/updateItem1.fxml"));
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
        productCategoryComboBox.getItems().clear();
        productCategoryComboBox.setItems(observableList);
    }
}
