package com;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class RemoveDiscountCodeController extends NavigationAdminCodes implements Initializable {

    @FXML private Button removeButton;
    @FXML private TableView<ProductAndCodesSelection> tableView;
    @FXML private TableColumn<ProductAndCodesSelection, CheckBox> checkBoxes;
    @FXML private TableColumn<ProductAndCodesSelection, ToggleButton> discountCodes;

    private ObservableList<ProductAndCodesSelection> observableList = FXCollections.observableArrayList();
    private ToggleGroup toggleGroup = new ToggleGroup();
    private Connection connection = DBConnection.getConnection();

    @FXML
    public void removeAction(ActionEvent event) throws SQLException {
        String query1 = "DELETE FROM codes WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query1);

        String query2 = "DELETE FROM per_user_codes_quantity WHERE code_id = ?";
        PreparedStatement preparedStatement2 = connection.prepareStatement(query2);

        ObservableList<ProductAndCodesSelection> removeItems = FXCollections.observableArrayList();
        int x = 0;
        for (ProductAndCodesSelection codes : observableList) {
            if (codes.getCheckBox().isSelected()) {
                HBox hBox = (HBox) observableList.get(x).getToggleButton().graphicProperty().getValue();
                Label label = (Label) hBox.getChildren().get(0);
                int id = Integer.parseInt(label.getText());
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                preparedStatement2.setInt(1, id);
                preparedStatement2.executeUpdate();
                removeItems.add(codes);
            }
            x++;
        }

        preparedStatement.close();
        tableView.getItems().removeAll(removeItems);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String query = "SELECT id, discount_code, amount, discount, quantity, type FROM codes";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CheckBox checkBox = new CheckBox("");
                String id = String.valueOf(resultSet.getInt(1));
                String discountCode = resultSet.getString(2);
                String amount = String.valueOf(resultSet.getDouble(3));
                String discount = String.valueOf(resultSet.getDouble(4));
                String quantity = String.valueOf(resultSet.getInt(5));
                String type = resultSet.getString(6);
                observableList.add(new ProductAndCodesSelection(checkBox, rowRendererCode(id, discountCode, amount, discount, quantity, type)));
            }
            checkBoxes.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
            discountCodes.setCellValueFactory(new PropertyValueFactory<>("toggleButton"));
            tableView.setItems(observableList);
            tableView.setSelectionModel(null);
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ToggleButton rowRendererCode(String id, String discountCode, String amount, String discount, String quantity, String type) {
        ToggleButton toggleButton = new ToggleButton("");
        toggleButton.getStyleClass().add("button-item");

        HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox-code-1");

        ImageView imageView = new ImageView(new Image(getClass().getResource("icons/discount-code-2.png").toExternalForm()));
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);

        Label idLabel = new Label(id);
        Label discountCodeLabel = new Label(discountCode);
        Label amountLabel = new Label(amount);
        Label discountLabel = new Label(discount);
        Label quantityLabel = new Label(quantity);
        Label typeLabel = new Label(type);
        idLabel.getStyleClass().add("label-code-id-3");
        discountCodeLabel.getStyleClass().add("label-code-discount-code-3");
        amountLabel.getStyleClass().add("label-code-amount-3");
        discountLabel.getStyleClass().add("label-code-discount-3");
        quantityLabel.getStyleClass().add("label-code-quantity-3");
        typeLabel.getStyleClass().add("label-code-type-3");

        hBox.getChildren().addAll(idLabel, imageView, discountCodeLabel, amountLabel, discountLabel, quantityLabel, typeLabel);
        toggleButton.graphicProperty().setValue(hBox);
        toggleButton.setToggleGroup(toggleGroup);
        return toggleButton;
    }
}
