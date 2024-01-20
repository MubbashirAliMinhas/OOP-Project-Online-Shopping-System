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
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class PlaceOrderController extends NavigationCustomer implements Initializable {

    @FXML private Button proceedButton, goBackButton, applyCodeButton;
    @FXML private ScrollPane scrollPane;
    @FXML private Label totalAmountLabel, validationLabel;
    @FXML private TextField discountCodeField;
    @FXML private ComboBox<String> paymentMethodComboBox;
    @FXML private TableView<ProductAndCodes> tableView;
    @FXML private TableColumn<ProductAndCodes, ToggleButton> products;

    private ObservableList<ProductAndCodes> observableList = FXCollections.observableArrayList();
    private ObservableList<String> paymentMethods = FXCollections.observableArrayList("Cash on delivery", "Wallet");
    private ToggleGroup toggleGroup = new ToggleGroup();
    private String email = CustomerDashboard.customer.email;
    private CartDB cartDB = CustomerDashboard.cartDB;
    private Connection connection = DBConnection.getConnection();

    private double totalAmount;
    private Cart cart = new Cart();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paymentMethodComboBox.getItems().removeAll();
        paymentMethodComboBox.getItems().addAll(paymentMethods);
        if (!cartDB.items.isEmpty()) {
            for (Item item : cartDB.items.values()) {
                String id = String.valueOf(item.id);
                String imagePath = item.imagePath;
                String name = item.name;
                double price = item.price;
                String quantity = String.valueOf(item.quantity);
                String category = item.category;
                totalAmount += price * Double.parseDouble(quantity);
                observableList.add(new ProductAndCodes(rowRenderer(id, imagePath, name, price, quantity, category)));
            }
            products.setCellValueFactory(new PropertyValueFactory<>("toggleButton"));
            tableView.setItems(observableList);
            tableView.setSelectionModel(null);
            totalAmountLabel.setText("Rs. " + totalAmount);
        }
    }

    @FXML
    public void switchToCart(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/cart.fxml"));
        goBackButton.getScene().setRoot(root);
    }

    @FXML
    public void proceedAction(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        boolean isEnoughBalance = true;
        if (paymentMethodComboBox.getValue() != null) {

            if (paymentMethodComboBox.getValue().equals("Wallet")) {
                String query1 = "SELECT balance FROM user_wallet WHERE email = '" + email + "'";
                PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
                ResultSet resultSet = preparedStatement1.executeQuery();
                resultSet.next();

                double balance = resultSet.getDouble(1);
                resultSet.close();
                preparedStatement1.close();

                if (balance >= totalAmount) {
                    String query2 = "UPDATE user_wallet SET balance = ? WHERE email = '" + email + "'";
                    PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                    preparedStatement2.setDouble(1, balance - totalAmount);
                    preparedStatement2.execute();
                    preparedStatement2.close();
                } else {
                    validationLabel.setVisible(true);
                    validationLabel.setText("Not enough balance in wallet");
                    isEnoughBalance = false;
                }
            }

            if (isEnoughBalance) {
                for (Item item : cartDB.items.values()) {
                    int id = item.id;
                    int quantity = item.quantity;
                    cart.items.add(item);
                    cart.totalAmount = String.valueOf(totalAmount);
                    cart.paymentMethod = paymentMethodComboBox.getValue();
                    cart.date = new java.util.Date().getTime();

                    String query3 = "SELECT quantity FROM inventory WHERE id = " + id;
                    PreparedStatement preparedStatement3 = connection.prepareStatement(query3);
                    ResultSet resultSet = preparedStatement3.executeQuery();
                    resultSet.next();
                    int invQuantity = resultSet.getInt(1) - quantity;
                    resultSet.close();
                    preparedStatement3.close();

                    String query4 = "UPDATE inventory set quantity = ? WHERE id = " + id;
                    PreparedStatement preparedStatement4 = connection.prepareStatement(query4);
                    preparedStatement4.setInt(1, invQuantity);
                    preparedStatement4.execute();
                    preparedStatement4.close();
                }
                Orders orders = (Orders) ObjectHandler.readObject(connection, "user_orders", "orders", email);
                orders.carts.add(cart);
                ObjectHandler.updateOrders(connection, email, orders);

                java.util.Date date = new java.util.Date();

                String query7 = "INSERT INTO sales (order_id, email, payment_method, order_date) VALUES(?, ?, ?, ?)";
                PreparedStatement preparedStatement7 = connection.prepareStatement(query7);
                preparedStatement7.setInt(1, orders.carts.size());
                preparedStatement7.setString(2, email);
                preparedStatement7.setString(3, paymentMethodComboBox.getValue());
                preparedStatement7.setLong(4, date.getTime());
                preparedStatement7.execute();
                preparedStatement7.close();

                System.out.println("Order successfully added!!");
                cartDB.items.clear();
                cartDB.quantityInCart.clear();
                cartDB.quantities.clear();
                cartDB.availability.clear();
                ObjectHandler.updateCart(connection);

                Parent root = FXMLLoader.load(getClass().getResource("views/customerDashboard.fxml"));
                goBackButton.getScene().setRoot(root);
            }
        } else {
            validationLabel.setVisible(true);
            validationLabel.setText("Please set payment method");
        }
    }

    @FXML
    public void applyCodeAction() throws SQLException {
        String query1 = "SELECT * FROM codes WHERE discount_code = '" + discountCodeField.getText() + "'";

        PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
        ResultSet resultSet = preparedStatement1.executeQuery();

        if (resultSet.next()) {

            int id = resultSet.getInt(1);
            String discountCode = resultSet.getString(2);
            double amount = resultSet.getDouble(3);
            double discount = resultSet.getDouble(4);
            int quantity = resultSet.getInt(5);
            String type = resultSet.getString(6);

            if (totalAmount >= amount) {
                if (type.equals("All users")) {
                    if (quantity != 0) {
                        String query2 = "UPDATE codes SET quantity = ? WHERE id = " + id;
                        PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                        preparedStatement2.setInt(1, --quantity);
                        preparedStatement2.execute();
                        preparedStatement2.close();

                        validationLabel.setVisible(true);
                        validationLabel.setText("Successfully Applied.");
                        totalAmount *= (100 - discount) / 100;
                        discountCodeField.setDisable(true);
                        applyCodeButton.setDisable(true);
                        totalAmountLabel.setText("Rs. " + totalAmount);
                    } else {
                        validationLabel.setVisible(true);
                        validationLabel.setText("Invalid Code.");
                    }
                } else {
                    String query3 = "SELECT quantity FROM per_user_codes_quantity WHERE (email = '" + email + "' AND code_id = " + id + ")";
                    PreparedStatement preparedStatement3 = connection.prepareStatement(query3);
                    ResultSet resultSet3 = preparedStatement3.executeQuery();
                    if (!resultSet3.next()) {
                        String query4 = "INSERT INTO per_user_codes_quantity (email, code_id, quantity) VALUES(?, ?, ?)";
                        PreparedStatement preparedStatement4 = connection.prepareStatement(query4);
                        preparedStatement4.setString(1, email);
                        preparedStatement4.setInt(2, id);
                        preparedStatement4.setInt(3, --quantity);
                        preparedStatement4.execute();
                        preparedStatement4.close();

                        validationLabel.setVisible(true);
                        validationLabel.setText("Successfully Applied.");
                        totalAmount *= (100 - discount) / 100;
                        discountCodeField.setDisable(true);
                        applyCodeButton.setDisable(true);
                        totalAmountLabel.setText("Rs. " + totalAmount);
                    } else {
                        int perUserCodeQuantity = resultSet3.getInt(1);
                        if (perUserCodeQuantity == 0) {
                            validationLabel.setVisible(true);
                            validationLabel.setText("you can no longer use this discount code");
                        } else {
                            String query5 = "UPDATE per_user_codes_quantity set quantity = ? WHERE (email = '" + email + "' AND code_id = " + id + ")";
                            PreparedStatement preparedStatement5 = connection.prepareStatement(query5);
                            preparedStatement5.setInt(1, --perUserCodeQuantity);
                            preparedStatement5.execute();
                            preparedStatement5.close();

                            validationLabel.setVisible(true);
                            validationLabel.setText("Successfully Applied.");
                            totalAmount *= (100 - discount) / 100;
                            discountCodeField.setDisable(true);
                            applyCodeButton.setDisable(true);
                            totalAmountLabel.setText("Rs. " + totalAmount);
                        }
                    }
                    resultSet3.close();
                    preparedStatement3.close();
                }
            } else {
                validationLabel.setVisible(true);
                validationLabel.setText("Total amount amount is less than Rs. " + amount);
            }
        } else {
            validationLabel.setVisible(true);
            validationLabel.setText("Invalid Code.");
        }
        resultSet.close();
        preparedStatement1.close();
    }

    public ToggleButton rowRenderer(String id, String imagePath, String name, double price, String quantity, String category) {
        ToggleButton toggleButton = new ToggleButton("");
        toggleButton.getStyleClass().add("button-item");

        HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox-product-2");

        Rectangle imageView = new Rectangle(0, 0, 40, 40);
        imageView.setArcWidth(10);
        imageView.setArcHeight(10);
        ImagePattern imagePattern = new ImagePattern(new Image(getClass().getResource(imagePath).toExternalForm()));
        imageView.setFill(imagePattern);

        price *= Integer.parseInt(quantity);

        Label idLabel = new Label(id);
        Label nameLabel = new Label(name);
        Label priceLabel = new Label("Rs. " + price);
        Label quantityLabel = new Label(quantity);
        Label categoryLabel = new Label(category);
        idLabel.getStyleClass().add("label-product-id-1");
        nameLabel.getStyleClass().add("label-product-name-1");
        priceLabel.getStyleClass().add("label-product-price-1");
        quantityLabel.getStyleClass().add("label-product-quantity-1");
        categoryLabel.getStyleClass().add("label-product-category-1");

        hBox.getChildren().addAll(idLabel, imageView, nameLabel, priceLabel, quantityLabel, categoryLabel);
        toggleButton.graphicProperty().setValue(hBox);
        toggleButton.setToggleGroup(toggleGroup);
        return toggleButton;
    }
}
