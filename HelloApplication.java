package com.example.inventorymanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.io.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class HelloApplication extends Application {
    Inventory inventory = new Inventory();


    //Function to add product
    private void handleAddProduct(TextField productNameField, TextField quantityField, TextField priceField) {
        try {
            String productName = productNameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());

            // Assuming inventory and addProduct method are properly initialized
            inventory.addProduct(new Product(productName, quantity, price));

            System.out.println("Product added");
            productNameField.clear();
            quantityField.clear();
            priceField.clear();
        } catch (NumberFormatException ex) {
            System.err.println("Error: Invalid input. Please enter valid numeric values.");
            ex.printStackTrace();
        } catch (Exception ex) {
            // Handle other exceptions if needed
            ex.printStackTrace();
        }
    }

    //Function to Update Product
    private void updateProduct(TextField name, TextField quantity, TextField price) {
        try {
            String updateProductName = name.getText();
            int newQuantity = Integer.parseInt(quantity.getText());
            double newPrice = Double.parseDouble(price.getText());

            // Assuming inventory and updateProduct method are properly initialized
            inventory.updateProduct(updateProductName, newQuantity, newPrice);

           name.clear();
           quantity.clear();
           price.clear();
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid input. Please enter valid numeric values for quantity and price.");
            e.printStackTrace();
        } catch (Exception e) {
            // Handle other exceptions if needed
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Function to search for a product and display in TableView
    private void SearchProduct(TextField searchProductNameField,TableView<Product> searchResultTable) {
        String searchProductName = searchProductNameField.getText().trim();

        if (searchProductName.isEmpty()) {
            System.out.println("Please enter a product name to search.");
            return; // Exit the function if the input is invalid
        }

        Product foundProduct = inventory.searchProduct(searchProductName);

        if (foundProduct != null) {
            // Clear existing items and add the found product to the TableView
            ObservableList<Product> data = FXCollections.observableArrayList(foundProduct);
            searchResultTable.setItems(data);
            System.out.println("Product found: " + foundProduct);
        } else {
            // Clear the TableView if the product is not found
            searchResultTable.getItems().clear();
            System.out.println("Product not found.");
        }

        searchProductNameField.clear();
    }


    // Function to place an order
    private void placeOrder(TextField productNameField, TextField quantityField, TextField priceField) {
        String productName = productNameField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String priceText = priceField.getText().trim();

        if (productName.isEmpty() || quantityText.isEmpty() || priceText.isEmpty()) {
            System.out.println("Please enter values for all fields.");
            return; // Exit the function if any field is empty
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            double price = Double.parseDouble(priceText);

            // Additional validation if needed (e.g., non-negative values)

            // Create a new product
            Product newOrder = new Product(productName, quantity, price);
            inventory.addOrder(newOrder);

            System.out.println("Order placed: " + newOrder);

            // Clear the text fields
            productNameField.clear();
            quantityField.clear();
            priceField.clear();
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numeric values for quantity and price.");
        }
    }

    // Function to Record Sale
    private void recordSale(TextField productNameField, TextField quantitySoldField) {
//        String productName = productNameField.getText().trim();
//        String quantitySoldText = quantitySoldField.getText().trim();


        String saleProductName = productNameField.getText();

        int saleQuantity = Integer.parseInt(quantitySoldField.getText());
        Product soldProduct = inventory.searchProduct(saleProductName);
        if (soldProduct != null && soldProduct.quantity >= saleQuantity) {
            soldProduct.quantity -= saleQuantity;
            inventory.recordSale(new Product(soldProduct.name, saleQuantity, soldProduct.price));
            System.out.println("Sale recorded successfully.");
        } else {
            System.out.println("Product not found or insufficient quantity.");
        }
    }


    //Function to Display Products

    private void displayProducts() {
        // Create a new stage for displaying products
        Stage productsStage = new Stage();
        productsStage.setTitle("Product Table View");

        // Create a TableView
        TableView<Product> tableView = new TableView<>();

        // Create columns for each property in the Product class
        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Add columns to the TableView
        tableView.getColumns().addAll(nameColumn, quantityColumn, priceColumn);

        // Create an ObservableList to hold your data
        ObservableList<Product> productData = FXCollections.observableArrayList(inventory.products);

        // Set the items in the table
        tableView.setItems(productData);

        // Create the scene and set it to the stage
        Scene productsScene = new Scene(new VBox(tableView), 400, 300);
        productsStage.setScene(productsScene);

        // Show the products stage
        productsStage.show();
    }

    // Function to display Sales History
    public void display_Sales_History(int n) {
        Stage salesHistoryStage = new Stage();
        salesHistoryStage.setTitle("Sales History Table View");

        if (inventory.salesHistory.isEmpty()) {
            System.out.println("No Sales Yet");
        } else {
            Stack<Product> tempStack = new Stack<>();
            int count = 0;

            // Pop elements from the salesHistory stack and add to the tempStack
            // to preserve the order for TableView
            while (!inventory.salesHistory.isEmpty() && count < n) {
                tempStack.push(inventory.salesHistory.pop());
                count++;
            }

            // Create a TableView
            TableView<Product> tableView = new TableView<>();

            // Create columns for each property in the Product class
            TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

            TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

            // Create an ObservableList to hold your sales data
            ObservableList<Product> salesData = FXCollections.observableArrayList(tempStack);

            // Set the items in the TableView
            tableView.setItems(salesData);

            // Add columns to the TableView
            tableView.getColumns().addAll(nameColumn, quantityColumn, priceColumn);

            // Create the scene and set it to the stage
            Scene salesHistoryScene = new Scene(new VBox(tableView), 400, 300);
            salesHistoryStage.setScene(salesHistoryScene);

            // Show the sales stage
            salesHistoryStage.show();

            // Push elements back to the salesHistory stack
            while (!tempStack.isEmpty()) {
                inventory.salesHistory.push(tempStack.pop());
            }
        }
    }



    @Override
    public void start(Stage stage) throws IOException {

        Scene loginscene,detailsscene,addProductScene,updateProductScene,searchProductScene,placeorderScene,saleProductScene,salesHistoryScene;
        // Title
        stage.setTitle("Inventory Management System");
        GridPane loginPane = new GridPane();
        loginPane.setStyle("-fx-background-color: #F5F5CC;");
        loginscene = new Scene(loginPane, 600, 600);
        Image logoImage = new Image("file:store.png");
        ImageView imageView = new ImageView(logoImage);
        loginPane.add(imageView, 0, 0, 2, 1);
        loginPane.setAlignment(Pos.CENTER);
        imageView.setFitWidth(350);
        imageView.setFitHeight(350);
        Label usernameLogin = new Label("Username");
        usernameLogin.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label passwordLogin = new Label("Password");
        passwordLogin.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: skyblue;");
        loginButton.setTextFill(Color.BLACK);
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button exit = new Button("Exit");
        exit.setStyle("-fx-background-color: #FF6961;");
        exit.setTextFill(Color.BLACK);
        exit.setOnAction(e -> System.exit(0));
        loginPane.add(usernameLogin, 0, 2);
        loginPane.add(passwordLogin, 0, 3);
        loginPane.add(usernameField, 1, 2);
        loginPane.add(passwordField, 1, 3);
        loginPane.setVgap(2);
        HBox hBoxLogin = new HBox();
        hBoxLogin.getChildren().addAll(exit,loginButton);
        loginPane.add(hBoxLogin, 1, 4);

        // Details Scene

        Label welcomelabel = new Label("Menu");
        welcomelabel.setFont(new Font("Arial", 18));

        Button add_productButton = new Button("Add Product");
        add_productButton.setStyle("-fx-background-color: #808080;");
        add_productButton.setTextFill(Color.BLACK);
        Button update_productButton = new Button("Update Product");
        update_productButton.setStyle("-fx-background-color: #FFA500");
        update_productButton.setTextFill(Color.BLACK);
        Button search_productButton = new Button("Search Product");
        search_productButton.setStyle("-fx-background-color: #808080;");
        search_productButton.setTextFill(Color.BLACK);
        Button display_productsButton = new Button("Display Products");
        display_productsButton.setStyle("-fx-background-color: #FFA500");
        display_productsButton.setTextFill(Color.BLACK);
        //Display products action
        display_productsButton.setOnAction(e->displayProducts());
        Button save_inventoryButton = new Button("Save Inventory");
        save_inventoryButton.setStyle("-fx-background-color: #808080;");
        save_inventoryButton.setTextFill(Color.BLACK);
        save_inventoryButton.setOnAction(e->inventory.saveToFile("src/inventory.txt"));
        Button place_ordersButton = new Button("Place Orders");
        place_ordersButton.setStyle("-fx-background-color: #FFA500");
        place_ordersButton.setTextFill(Color.BLACK);
        Button process_ordersButton = new Button("Process Orders");
        process_ordersButton.setStyle("-fx-background-color: #808080;");
        process_ordersButton.setTextFill(Color.BLACK);
        process_ordersButton.setOnAction(e->inventory.processOrders());
        Button record_saleButton = new Button("Record Sale");
        record_saleButton.setStyle("-fx-background-color: #FFA500");
        record_saleButton.setTextFill(Color.BLACK);
        Button display_sales_historyButton = new Button("Display Sales History");
        display_sales_historyButton.setStyle("-fx-background-color: #808080;");
        display_sales_historyButton.setTextFill(Color.BLACK);
        Button checkReorderPointButton = new Button("Check Reorder Point");
        checkReorderPointButton.setStyle("-fx-background-color: #FFA500");
        checkReorderPointButton.setTextFill(Color.BLACK);
        checkReorderPointButton.setOnAction(e->inventory.checkReorderPoint());
        Button process_reordersButton = new Button("Process Reorders");
        process_reordersButton.setStyle("-fx-background-color: #808080;");
        process_reordersButton.setTextFill(Color.BLACK);
        process_reordersButton.setOnAction(e->inventory.processReorders());
        Button backButton1= new Button("Back");
        backButton1.setStyle("-fx-background-color: #FF6961;");
        backButton1.setTextFill(Color.BLACK);
        GridPane detailspane = new GridPane();
        detailspane.setStyle("-fx-background-color: #F5F5CC;");
        detailspane .setPadding(new Insets(20, 20, 20, 20));
        detailsscene = new Scene(detailspane , 600, 600);
        detailspane.add(welcomelabel, 0, 0, 2, 1);
        detailspane.add(add_productButton, 0, 1);
        detailspane.add(update_productButton, 0, 2);
        detailspane.add(search_productButton, 0, 3);
        detailspane.add(display_productsButton, 0, 4);
        detailspane.add(save_inventoryButton, 0, 5);
        detailspane.add(place_ordersButton, 0, 6);
        detailspane.add(process_ordersButton, 0, 7);
        detailspane.add(record_saleButton, 0, 8);
        detailspane.add(display_sales_historyButton, 0, 9);
        detailspane.add(checkReorderPointButton, 0, 10);
        detailspane.add(process_reordersButton, 0, 11);
        detailspane.add(backButton1, 0, 12);

        detailspane.setVgap(10);
        backButton1.setOnAction(e -> stage.setScene(loginscene));
        detailspane.setAlignment(Pos.CENTER);


        // Add Product Scene

        Label productNameLabel = new Label("Product Name:");
        Label quantityLabel = new Label("Quantity:");
        Label priceLabel = new Label("Price of one unit:");
        productNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        quantityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField productNameField = new TextField();
        TextField quantityField = new TextField();
        TextField priceField = new TextField();
        Button addProductButton = new Button("Add Product");
        //Add product action
        addProductButton.setOnAction(e -> handleAddProduct(productNameField, quantityField, priceField));
        Button backButton2=new Button("Back");
        backButton2.setStyle("-fx-background-color: #FF6961;");
        backButton2.setTextFill(Color.BLACK);
        GridPane addProductpane = new GridPane();
        addProductpane.setStyle("-fx-background-color: #F5F5CC;");
        addProductpane.setPadding(new Insets(20, 20, 20, 20));
        addProductpane.setVgap(10);
        addProductpane.setHgap(10);
        addProductpane.add(productNameLabel, 0, 0);
        addProductpane.add(productNameField, 1, 0);
        addProductpane.add(quantityLabel, 0, 1);
        addProductpane.add(quantityField, 1, 1);
        addProductpane.add(priceLabel, 0, 2);
        addProductpane.add(priceField, 1, 2);
        HBox hBoxaddProduct = new HBox();
        hBoxaddProduct.getChildren().addAll(addProductButton,backButton2);
        addProductpane.add(hBoxaddProduct, 1, 3);
        addProductScene = new Scene(addProductpane, 500, 400);
        backButton2.setOnAction(e->stage.setScene(detailsscene));
        add_productButton.setOnAction(e->stage.setScene(addProductScene));

        // Update Product Scene

        Label updateproductNameLabel = new Label("Product Name:");
        Label updatequantityLabel = new Label("Quantity:");
        Label updatepriceLabel = new Label("Price of one unit:");
        updateproductNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        updatequantityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        updatepriceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField updateproductNameField = new TextField();
        TextField updatequantityField = new TextField();
        TextField updatepriceField = new TextField();
        Button updateProductButton = new Button("Update Product");

        //Update Product Button
        updateProductButton.setOnAction(e->updateProduct(updateproductNameField,updatequantityField,updatepriceField ));

        Button backButton3=new Button("Back");
        backButton3.setStyle("-fx-background-color: #FF6961;");
        backButton3.setTextFill(Color.BLACK);
        GridPane updateProductpane = new GridPane();
        updateProductpane.setStyle("-fx-background-color: #F5F5CC;");
        updateProductpane.setPadding(new Insets(20, 20, 20, 20));
        updateProductpane.setVgap(10);
        updateProductpane.setHgap(10);
        updateProductpane.add(updateproductNameLabel, 0, 0);
        updateProductpane.add(updateproductNameField, 1, 0);
        updateProductpane.add(updatequantityLabel, 0, 1);
        updateProductpane.add(updatequantityField, 1, 1);
        updateProductpane.add(updatepriceLabel, 0, 2);
        updateProductpane.add(updatepriceField, 1, 2);
        HBox hBoxupdateProduct= new HBox();
        hBoxupdateProduct.getChildren().addAll(updateProductButton,backButton3);
        updateProductpane.add(hBoxupdateProduct, 1, 3);
        updateProductScene = new Scene(updateProductpane, 500, 400);
        backButton3.setOnAction(e->stage.setScene(detailsscene));
        update_productButton.setOnAction(e->stage.setScene(updateProductScene));

       // Search Product Scene

        Label searchproductNameLabel = new Label("Product Name:");
        searchproductNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField searchproductNameField = new TextField();
        Button searchProductButton = new Button("Search Product");
        Button backButton4=new Button("Back");
        backButton4.setStyle("-fx-background-color: #FF6961;");
        backButton4.setTextFill(Color.BLACK);
        // Create a TableView
        TableView<Product> searchResultTable = new TableView<>();

        // Set up the TableView with columns
        TableColumn<Product, String> productNameColumn = new TableColumn<>("Product Name");
        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");

        // Set up the cell value factories
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Add columns to the TableView
        searchResultTable.getColumns().addAll(productNameColumn, quantityColumn, priceColumn);

        searchProductButton.setOnAction(e -> SearchProduct(searchproductNameField,searchResultTable));


        GridPane searchProductpane = new GridPane();
        searchProductpane.setStyle("-fx-background-color: #F5F5CC;");
        searchProductpane.setPadding(new Insets(20, 20, 20, 20));
        searchProductpane.setVgap(10);
        searchProductpane.setHgap(10);
        searchProductpane.add(searchproductNameLabel, 0, 0);
        searchProductpane.add(searchproductNameField, 1, 0);
        HBox hBoxsearchProduct= new HBox();
        hBoxsearchProduct.getChildren().addAll(searchProductButton,backButton4);
        searchProductpane.add(hBoxsearchProduct, 1, 1);
        searchProductpane.add(searchResultTable, 0, 2, 2, 1);
        searchProductScene = new Scene(searchProductpane, 500, 400);
        backButton4.setOnAction(e->stage.setScene(detailsscene));
        search_productButton.setOnAction(e->stage.setScene(searchProductScene));


        // Place Order Scene

        Label placeorderNameLabel = new Label("Product Name:");
        Label placeorderquantityLabel = new Label("Quantity:");
        Label placeorderpriceLabel = new Label("Price of one unit:");
        placeorderNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        placeorderquantityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        placeorderpriceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField placeorderNameField = new TextField();
        TextField placeorderquantityField = new TextField();
        TextField placeorderpriceField = new TextField();
        Button placeorderProductButton = new Button("Place Order");
        placeorderProductButton.setOnAction(e -> placeOrder(placeorderNameField,placeorderquantityField,placeorderpriceField));
        Button backButton5=new Button("Back");
        backButton5.setStyle("-fx-background-color: #FF6961;");
        backButton5.setTextFill(Color.BLACK);
        GridPane placeorderpane = new GridPane();
        placeorderpane.setStyle("-fx-background-color: #F5F5CC;");
        placeorderpane.setPadding(new Insets(20, 20, 20, 20));
        placeorderpane.setVgap(10);
        placeorderpane.setHgap(10);
        placeorderpane.add(placeorderNameLabel, 0, 0);
        placeorderpane.add(placeorderNameField, 1, 0);
        placeorderpane.add(placeorderquantityLabel, 0, 1);
        placeorderpane.add(placeorderquantityField, 1, 1);
        placeorderpane.add(placeorderpriceLabel, 0, 2);
        placeorderpane.add(placeorderpriceField, 1, 2);
        HBox hBoxplaceorder= new HBox();
        hBoxplaceorder.getChildren().addAll(placeorderProductButton,backButton5);
        placeorderpane.add(hBoxplaceorder, 1, 3);
        placeorderScene = new Scene(placeorderpane, 500, 400);
        backButton5.setOnAction(e->stage.setScene(detailsscene));
        place_ordersButton.setOnAction(e->stage.setScene(placeorderScene));

        // Record Sale Scene

        Label saleproductNameLabel = new Label("Product Name:");
        Label salequantityLabel = new Label("Quantity Sold:");
        saleproductNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        salequantityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField saleproductNameField = new TextField();
        TextField salequantityField = new TextField();
        Button saleProductButton = new Button("Record Sale");
        saleProductButton.setOnAction(e->{recordSale(saleproductNameField,salequantityField);
         saleproductNameField.clear();
        salequantityField.clear();});
        Button backButton6=new Button("Back");
        backButton6.setStyle("-fx-background-color: #FF6961;");
        backButton6.setTextFill(Color.BLACK);
        GridPane saleProductpane = new GridPane();
        saleProductpane.setStyle("-fx-background-color: #F5F5CC;");
        saleProductpane.setPadding(new Insets(20, 20, 20, 20));
        saleProductpane.setVgap(10);
        saleProductpane.setHgap(10);
        saleProductpane.add(saleproductNameLabel, 0, 0);
        saleProductpane.add(saleproductNameField, 1, 0);
        saleProductpane.add(salequantityLabel, 0, 1);
        saleProductpane.add(salequantityField, 1, 1);
        HBox hBoxsaleProduct= new HBox();
        hBoxsaleProduct.getChildren().addAll(saleProductButton,backButton6);
        saleProductpane.add(hBoxsaleProduct, 1, 3);
        saleProductScene = new Scene(saleProductpane, 500, 400);
        backButton6.setOnAction(e->stage.setScene(detailsscene));
        record_saleButton.setOnAction(e->stage.setScene(saleProductScene));

        // Display Sales History Scene

        Label salesHistoryLabel = new Label("Enter number of sales records to display:");
        salesHistoryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField salesHistoryNameField = new TextField();
        Button salesHistoryButton = new Button("Display");
        salesHistoryButton.setOnAction((e -> {
            int numberOfRecords = Integer.parseInt(salesHistoryNameField.getText());
            display_Sales_History(numberOfRecords);
            salesHistoryNameField.clear();
        }));
        Button backbutton7 = new Button("Back");
        backbutton7.setStyle("-fx-background-color: #FF6961;");
        backbutton7.setTextFill(Color.BLACK);
        GridPane salesHistorypane = new GridPane();
        salesHistorypane.setStyle("-fx-background-color: #F5F5CC;");
        salesHistorypane.setPadding(new Insets(20, 20, 20, 20));
        salesHistorypane.setVgap(10);
        salesHistorypane.setHgap(10);
        salesHistorypane.add(salesHistoryLabel, 0, 0);
        salesHistorypane.add(salesHistoryNameField, 1, 0);
        HBox hBoxsalesHistory = new HBox();
        hBoxsalesHistory.getChildren().addAll(salesHistoryButton, backbutton7);
        salesHistorypane.add(hBoxsalesHistory, 1, 2);
        salesHistoryScene = new Scene(salesHistorypane, 500, 400);
        backbutton7.setOnAction(e -> stage.setScene(detailsscene));
        display_sales_historyButton.setOnAction(e -> stage.setScene(salesHistoryScene));

        // Login Search Implementation

        //login action
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loginButton.setOnAction(event -> {
                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    File file1 = new File("src/LoginSearch");
                    ArrayList<String> usernames = new ArrayList<>();
                    ArrayList<String> passwords = new ArrayList<>();
                    try {
                        Scanner sc = new Scanner(file1);
                        while (sc.hasNextLine()) {
                            String line = sc.nextLine();
                            String[] check = line.split(" ");
                            for (String Check :check) {
                                String[] parts = Check.split(",");
                                if (parts.length == 2) {
                                    String user = parts[0];
                                    String pass = parts[1];
                                    usernames.add(user);
                                    passwords.add(pass);
                                }
                            }
                        }

                        if (LoginCheck(username, password, usernames, passwords)) {
                            System.out.println("Successfully Logged In");
                            stage.setScene(detailsscene);
                            usernameField.clear();
                            passwordField.clear();
                        } else {
                            System.out.println("Invalid username or password");
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("File not found!!!!");
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        stage.setScene(loginscene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    private boolean LoginCheck(String username, String password, ArrayList<String> userName, ArrayList<String> passworD) {
        for (int i = 0; i < userName.size(); i++) {
            if (username.equals(userName.get(i)) && password.equals(passworD.get(i))) {
                return true;
            }
        }
        return false;
    }
}
