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
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class CartController extends NavigationCustomer implements Initializable {

    @FXML private Button removeButton, placeOrderButton, updateButton;
    @FXML private TableView<ProductAndCodesSelection> tableView;
    @FXML private TableColumn<ProductAndCodesSelection, CheckBox> checkBoxes;
    @FXML private TableColumn<ProductAndCodesSelection, ToggleButton> products;

    private ObservableList<ProductAndCodesSelection> observableList = FXCollections.observableArrayList();
    private HashMap<Integer, ProductAndCodesSelection> selectedItems = new HashMap<>();
    private HashMap<Integer, Integer> cartIndexes = new HashMap<>();
    private ToggleGroup toggleGroup = new ToggleGroup();
    private CartDB cartDB = CustomerDashboard.cartDB;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!cartDB.items.isEmpty()) {
            int x = 0;
            for (int itemId : cartDB.items.keySet()) {
                cartIndexes.put(itemId, x++);
            }

            setPlaceOrderState();
            for (int itemId : cartDB.items.keySet()) {
                CheckBox checkBox = new CheckBox("");
                int id = cartDB.items.get(itemId).id;
                String imagePath = cartDB.items.get(itemId).imagePath;
                String name = cartDB.items.get(itemId).name;
                double price = cartDB.items.get(itemId).price;
                int quantity = cartDB.items.get(itemId).quantity;
                String category = cartDB.items.get(itemId).category;
                ProductAndCodesSelection product = new ProductAndCodesSelection(checkBox, rowRenderer(id, imagePath, name, price, quantity, category, cartDB.availability.get(itemId)));
                observableList.add(product);
                checkBox.setUserData(itemId);
                checkBox.setOnAction(e -> {
                    if (checkBox.isSelected()) {
                        selectedItems.put(cartIndexes.get(itemId), product);
                    } else {
                        selectedItems.remove(cartIndexes.get(itemId));
                    }
                });
            }
            checkBoxes.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
            products.setCellValueFactory(new PropertyValueFactory<>("toggleButton"));
            tableView.setItems(observableList);
            tableView.setSelectionModel(null);
        } else {
            placeOrderButton.setDisable(true);
        }
    }

    @FXML
    public void switchToPlaceOrder(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/placeOrder.fxml"));
        placeOrderButton.getScene().setRoot(root);
    }
    
    @FXML
    public void setPlaceOrderState() {
        placeOrderButton.setDisable(false);
        for (ArrayList<Boolean> availabilityList : cartDB.availability.values()) {
            if (!availabilityList.get(1)) {
                placeOrderButton.setDisable(true);
            }
        }
    }

    public ToggleButton rowRenderer(int id, String imagePath, String name, double price, int quantity, String category, ArrayList<Boolean> availabilityList) {
        ToggleButton toggleButton = new ToggleButton("");
        toggleButton.getStyleClass().add("button-item");

        HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox-product-2");

        Rectangle imageView = new Rectangle(0, 0, 40, 40);
        imageView.setArcWidth(10);
        imageView.setArcHeight(10);
        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource(imagePath).toExternalForm()));
        imageView.setFill(imagePattern);

        price *= quantity;

        Label idLabel = new Label(String.valueOf(id));
        Label nameLabel = new Label(name);
        Label priceLabel = new Label("Rs. " + price);
        Label quantityLabel = new Label(String.valueOf(quantity));
        Label categoryLabel = new Label(category);
        idLabel.getStyleClass().add("label-product-id-1");
        nameLabel.getStyleClass().add("label-product-name-1");
        priceLabel.getStyleClass().add("label-product-price-1");
        quantityLabel.getStyleClass().add("label-product-quantity-1");
        categoryLabel.getStyleClass().add("label-product-category-1");

        VBox vBoxName = new VBox();
        vBoxName.getStyleClass().add("vbox-product-name");
        Label availabilityLabel = new Label();
        availabilityLabel.getStyleClass().add("label-product-availability");

        if (!availabilityList.get(0)) {
            availabilityLabel.setText("Product not available");
            vBoxName.getChildren().addAll(nameLabel, availabilityLabel);
        } else if (cartDB.quantities.get(id) == 0) {
            availabilityLabel.setText("Out of stock");
            vBoxName.getChildren().addAll(nameLabel, availabilityLabel);
        } else if (!availabilityList.get(1)) {
            availabilityLabel.setText("Stock is low");
            vBoxName.getChildren().addAll(nameLabel, availabilityLabel);
        } else {
            vBoxName.getChildren().add(nameLabel);
        }

        hBox.getChildren().addAll(idLabel, imageView, vBoxName, priceLabel, quantityLabel, categoryLabel);
        toggleButton.graphicProperty().setValue(hBox);
        toggleButton.setToggleGroup(toggleGroup);

        toggleButton.setOnAction(e -> {
            if (toggleButton.isSelected() && (!cartDB.availability.get(id).get(0) || cartDB.quantities.get(id) == 0)) {
                updateButton.setDisable(true);
            } else {
                updateButton.setDisable(false);
            }
        });
        return toggleButton;
    }

    @FXML
    public void removeAction(ActionEvent event) {
        selectedItems.forEach((cartIndex, product) -> {
            int id = (Integer) product.getCheckBox().getUserData();
            cartDB.quantityInCart.remove(id);
            cartDB.quantities.remove(id);
            cartDB.items.remove(id);
            cartDB.availability.remove(id);
            tableView.getItems().remove(product);
        });
        selectedItems.clear();
        setPlaceOrderState();
        if (cartDB.items.isEmpty()) {
            placeOrderButton.setDisable(true);
        }
    }

    @FXML
    public void updateAction(ActionEvent event) throws IOException {
        int id = 0;
        int x = 0;
        for (ProductAndCodesSelection product : observableList) {
            if (product.getToggleButton().isSelected()) {
                id = (Integer) product.getCheckBox().getUserData();
                break;
            }
            x++;
        }

        AddUpdateCartAlert.update(id, x, selectedItems, updateButton.getScene());
    }

    @FXML
    public void preservePreviousState(int index, HashMap<Integer, ProductAndCodesSelection> selectedItems) {
        if (!selectedItems.isEmpty()) {
            if (selectedItems.containsKey(index)) {
                selectedItems.get(index).setToggleButton(observableList.get(index).getToggleButton());
            }
            selectedItems.forEach((cartIndex, product) -> observableList.set(cartIndex, product));
        }

        observableList.get(index).getToggleButton().setSelected(true);
        tableView.scrollTo(index);
    }
}
