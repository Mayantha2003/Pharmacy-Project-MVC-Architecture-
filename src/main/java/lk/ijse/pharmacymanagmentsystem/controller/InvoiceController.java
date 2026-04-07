package lk.ijse.pharmacymanagmentsystem.controller;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lk.ijse.pharmacymanagmentsystem.dbc.DBConnection;
import lk.ijse.pharmacymanagmentsystem.dto.BatchDTO;
import lk.ijse.pharmacymanagmentsystem.dto.CustomerDTO;
import lk.ijse.pharmacymanagmentsystem.dto.InvoiceDTO;
import lk.ijse.pharmacymanagmentsystem.dto.InvoiceDetailDTO;
import lk.ijse.pharmacymanagmentsystem.model.BatchModel;
import lk.ijse.pharmacymanagmentsystem.model.CustomerModel;
import lk.ijse.pharmacymanagmentsystem.model.InvoiceModel;
import lk.ijse.pharmacymanagmentsystem.model.ProductModel;
import lk.ijse.pharmacymanagmentsystem.util.SessionManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class InvoiceController implements Initializable {

    @FXML
    private TextField invoiceNumberField;
    @FXML
    private DatePicker invoiceDatePicker;
    @FXML
    private ComboBox<String> customerNameField;

    @FXML
    private ComboBox<String> productCombo;
    @FXML
    private ComboBox<String> batchCombo;
    @FXML
    private TextField availableQtyField;
    @FXML
    private TextField sellingPriceField;
    @FXML
    private TextField lineQtyField;

    @FXML
    private TableView<InvoiceDetailDTO> cartTable;
    @FXML
    private TableColumn<InvoiceDetailDTO, Integer> cartNoCol;
    @FXML
    private TableColumn<InvoiceDetailDTO, String> cartProductCol;
    @FXML
    private TableColumn<InvoiceDetailDTO, String> cartBatchCol;
    @FXML
    private TableColumn<InvoiceDetailDTO, String> cartExpiryCol;
    @FXML
    private TableColumn<InvoiceDetailDTO, Integer> cartQtyCol;
    @FXML
    private TableColumn<InvoiceDetailDTO, Double> cartUnitPriceCol;
    @FXML
    private TableColumn<InvoiceDetailDTO, Double> cartLineTotal;
    @FXML
    private TableColumn<InvoiceDetailDTO, Void> cartActionsCol;

    @FXML
    private Label totalItemsLabel;
    @FXML
    private Label totalQtyLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private TextField discountPercentField;
    @FXML
    private Label discountAmountLabel;
    @FXML
    private Label grandTotalLabel;

    @FXML
    private ComboBox<String> paymentMethodCombo;
    @FXML
    private TextField paidAmountField;
    @FXML
    private Label balanceLabel;

    private ObservableList<InvoiceDetailDTO> cartList = FXCollections.observableArrayList();

    private ProductModel productModel = new ProductModel();
    private BatchModel batchModel = new BatchModel();
    private CustomerModel customerModel = new CustomerModel();
    private InvoiceModel invoiceModel = new InvoiceModel();

    private long selectedBatchId;
    private double selectedSellingPrice;
    private String selectedProductName;
    private LocalDate selectedExpiryDate;

    private long lastSavedInvoiceId = -1;

    private boolean isUpdating = false;

    ObservableList<String> allCustomerNames = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        invoiceDatePicker.setValue(LocalDate.now());
        invoiceDatePicker.setEditable(false);
        invoiceDatePicker.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
        });

        loadInvoiceNumber();
        loadPaymentMethods();
        loadCustomerNames();
        setupCartTable();
        cartTable.setItems(cartList);

        cartNoCol.setCellFactory(column -> new TableCell<InvoiceDetailDTO, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        cartList.addListener((ListChangeListener<InvoiceDetailDTO>) c -> updateSummary());
        discountPercentField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                discountPercentField.setText(oldVal);
            } else {
                calculateDiscount();
            }
        });

        paidAmountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                paidAmountField.setText(oldVal);
            } else {
                calculateBalance();
            }
        });

        productCombo.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (isUpdating) {
                return;
            }
            String typed = newValue == null ? "" : newValue.trim();

            if (typed.isEmpty()) {
                Platform.runLater(() -> {
                    isUpdating = true;
                    productCombo.getItems().clear();
                    productCombo.hide();
                    batchCombo.getItems().clear();
                    clearBatchDetails();
                    isUpdating = false;
                });
                return;
            }

            try {
                List<String> suggestions = batchModel.getProductSuggestionsStartingWith(typed);

                Platform.runLater(() -> {
                    isUpdating = true;

                    String currentText = productCombo.getEditor().getText();
                    int caret = productCombo.getEditor().getCaretPosition();

                    if (!suggestions.isEmpty()) {
                        productCombo.setItems(FXCollections.observableArrayList(suggestions));

                        productCombo.getEditor().setText(currentText);
                        productCombo.getEditor().positionCaret(caret);

                        if (!productCombo.isShowing()) {
                            productCombo.show();
                        }
                    } else {
                        productCombo.getItems().clear();
                        productCombo.hide();
                    }
                    isUpdating = false;
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        productCombo.setOnAction(event -> {
            if (isUpdating) {
                return;
            }

            String selectedProduct = productCombo.getSelectionModel().getSelectedItem();

            if (selectedProduct == null || selectedProduct.trim().isEmpty()) {
                return;
            }

            try {
                List<String> batches = batchModel.searchAvailableBatches(selectedProduct.trim());

                Platform.runLater(() -> {
                    isUpdating = true;
                    batchCombo.getSelectionModel().clearSelection();

                    if (batches.isEmpty()) {
                        batchCombo.getItems().clear();
                        showAlert("No Stock", "No available batch for " + selectedProduct);
                        clearBatchDetails();
                    } else {
                        batchCombo.setItems(FXCollections.observableArrayList(batches));
                        batchCombo.show();
                    }
                    isUpdating = false;
                });
            } catch (SQLException e) {
                showAlert("Error", "Failed to load batches!");
            }
        });

        batchCombo.setOnAction(event -> handleBatchSelect());

    }

    private void loadBatchesForProduct(String productName) {
        try {
            List<String> batches = batchModel.searchAvailableBatches(productName);
            batchCombo.getItems().clear();

            if (batches.isEmpty()) {
                showAlert("No Stock", "No available batch for " + productName);
                clearBatchDetails();
            } else {
                batchCombo.setItems(FXCollections.observableArrayList(batches));
                batchCombo.show();
                batchCombo.requestFocus();
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load batches!");
        }
    }

    private void loadCustomerNames() {
        try {
            List<CustomerDTO> customers = customerModel.getAllCustomers();
            ObservableList<String> allCustomerNames = FXCollections.observableArrayList();
            for (CustomerDTO customer : customers) {
                allCustomerNames.add(customer.getCustomerName());
            }

            FilteredList<String> filteredItems = new FilteredList<>(allCustomerNames, p -> true);
            customerNameField.setItems(filteredItems);

            customerNameField.setEditable(true);

            customerNameField.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    if (customerNameField.getSelectionModel().getSelectedItem() != null
                            && customerNameField.getSelectionModel().getSelectedItem().equals(customerNameField.getEditor().getText())) {
                        return;
                    }

                    if (newValue == null || newValue.isEmpty()) {
                        filteredItems.setPredicate(item -> true);
                        customerNameField.hide();
                    } else {
                        String filter = newValue.toLowerCase();
                        filteredItems.setPredicate(item -> item.toLowerCase().contains(filter));

                        if (!filteredItems.isEmpty()) {
                            customerNameField.show();
                        } else {
                            customerNameField.hide();
                        }
                    }
                });
            });

            customerNameField.setValue("Cash Customer");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load customers!");
        }
    }

    @FXML
    private void handleBatchSelect() {
        String selected = batchCombo.getValue();
        if (selected == null || selected.trim().isEmpty()) {
            return;
        }

        try {
            BatchDTO batch = batchModel.getBatchByDisplay(selected);
            if (batch != null) {
                selectedBatchId = batch.getBatchId();
                selectedProductName = batch.getProductName();
                selectedExpiryDate = batch.getExpiryDate();
                selectedSellingPrice = batch.getSellingPrice();

                int dbQty = batch.getQtyRemaining();

                int qtyInCart = 0;
                for (InvoiceDetailDTO item : cartList) {
                    if (item.getBatchId() == selectedBatchId) {
                        qtyInCart += item.getQuantity();
                    }
                }

                int realAvailableQty = dbQty - qtyInCart;

                availableQtyField.setText(String.valueOf(realAvailableQty));
                sellingPriceField.setText(String.format("%.2f", selectedSellingPrice));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load batch details!");
        }
    }

    private void loadInvoiceNumber() {
        try {
            String nextNumber = invoiceModel.generateNextInvoiceNumber();
            invoiceNumberField.setText(nextNumber);
        } catch (SQLException e) {
            invoiceNumberField.setText("INV-" + LocalDate.now().getYear() + "-001");
        }
    }

    private void loadPaymentMethods() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList("Cash", "Card", "Bank Transfer", "Cheque"));
        paymentMethodCombo.setValue("Cash");
    }

    @FXML
    private void handleAddToCart() {
        if (!validateLine()) {
            return;
        }

        try {
            int qty = Integer.parseInt(lineQtyField.getText().trim());
            int available = Integer.parseInt(availableQtyField.getText());

            if (qty <= 0) {
                showAlert("Invalid Quantity", "Please enter a quantity greater than zero.");
                return;
            }

            if (qty > available) {
                showAlert("Out of Stock", "Only " + available + " units available in this batch.");
                return;
            }

            for (InvoiceDetailDTO item : cartList) {
                if (item.getBatchId() == selectedBatchId) {

                    int newTotalQty = item.getQuantity() + qty;
                    item.setQuantity(newTotalQty);
                    item.calculateLineTotal();

                    cartTable.refresh();
                    updateSummary();
                    clearLineFields();
                    return;
                }
            }

            InvoiceDetailDTO detail = new InvoiceDetailDTO();
            detail.setBatchId(selectedBatchId);
            detail.setProductName(selectedProductName);
            detail.setBatchNumber(batchCombo.getValue());
            detail.setExpiryDate(selectedExpiryDate);
            detail.setQuantity(qty);
            detail.setSellingPrice(selectedSellingPrice);
            detail.calculateLineTotal();

            cartList.add(detail);
            clearLineFields();
            updateSummary();

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for quantity.");
        }
    }

    private void setupCartTable() {

        cartProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartBatchCol.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        cartExpiryCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        cartQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartUnitPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        cartLineTotal.setCellValueFactory(new PropertyValueFactory<>("lineTotal"));

        cartActionsCol.setCellFactory(param -> new TableCell<InvoiceDetailDTO, Void>() {
            private final Button removeBtn = new Button("Remove");

            {
                removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                removeBtn.setOnAction(event -> {
                    InvoiceDetailDTO detail = getTableView().getItems().get(getIndex());
                    cartList.remove(detail);
                    updateSummary();
                    cartTable.refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });
    }

    private void updateSummary() {

        int items = cartList.size();
        int qty = cartList.stream().mapToInt(InvoiceDetailDTO::getQuantity).sum();
        double subtotal = cartList.stream().mapToDouble(InvoiceDetailDTO::getLineTotal).sum();

        totalItemsLabel.setText(String.valueOf(items));
        totalQtyLabel.setText(String.valueOf(qty));
        subtotalLabel.setText("Rs. " + String.format("%.2f", subtotal));

        calculateDiscount();
    }

    private void calculateDiscount() {
        try {
            double subtotal = cartList.stream().mapToDouble(InvoiceDetailDTO::getLineTotal).sum();

            String discPercentText = discountPercentField.getText().trim();
            double discountPercent = discPercentText.isEmpty() ? 0 : Double.parseDouble(discPercentText);

            double discountAmount = subtotal * (discountPercent / 100.0);
            double grandTotal = subtotal - discountAmount;

            discountAmountLabel.setText(String.format("Rs. %.2f", discountAmount));
            grandTotalLabel.setText(String.format("Rs. %.2f", grandTotal));

            calculateBalance();

        } catch (NumberFormatException e) {
            discountAmountLabel.setText("Rs. 0.00");
            double subtotal = cartList.stream().mapToDouble(InvoiceDetailDTO::getLineTotal).sum();
            grandTotalLabel.setText(String.format("Rs. %.2f", subtotal));
        }
    }

    private void calculateBalance() {
        try {
            String totalText = grandTotalLabel.getText().replace("Rs. ", "").replace(",", "").trim();
            double grandTotal = totalText.isEmpty() ? 0 : Double.parseDouble(totalText);

            String paidText = paidAmountField.getText().trim();
            double paid = paidText.isEmpty() ? 0 : Double.parseDouble(paidText);

            double balance = paid - grandTotal;

            balanceLabel.setText(String.format("Rs. %.2f", balance));

        } catch (NumberFormatException e) {
            balanceLabel.setText("Rs. 0.00");
        }
    }

    @FXML
    private void handleSaveInvoice() {
        if (cartList.isEmpty()) {
            showAlert("Required", "Cart is empty!");
            return;
        }

        try {
            String cleanTotal = grandTotalLabel.getText().replace("Rs. ", "").replace(",", "").trim();
            double grandTotal = cleanTotal.isEmpty() ? 0 : Double.parseDouble(cleanTotal);

            String paidText = paidAmountField.getText().trim();
            if (paidText.isEmpty()) {
                showAlert("Required", "Please enter the paid amount!");
                return;
            }
            double paid = Double.parseDouble(paidText);

            if (paid < grandTotal) {
                showAlert("Insufficient Payment", "The paid amount is less than the total bill amount!");
                return;
            }

            String rawText = discountAmountLabel.getText().replace("Rs. ", "").replace(",", "").trim();

            if (rawText.indexOf('.') != rawText.lastIndexOf('.')) {
                int firstDot = rawText.indexOf('.');
                String part1 = rawText.substring(0, firstDot + 1);
                String part2 = rawText.substring(firstDot + 1).replace(".", "");
                rawText = part1 + part2;
            }

            java.math.BigDecimal discountValue;

            try {
                discountValue = rawText.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(rawText);
            } catch (NumberFormatException e) {
                System.err.println("Fixing invalid discount format...");
                discountValue = java.math.BigDecimal.ZERO;
            }

            InvoiceDTO invoice = new InvoiceDTO();

            invoice.setInvoiceNumber(invoiceNumberField.getText().trim());
            invoice.setInvoiceDate(invoiceDatePicker.getValue());

            String selectedCustomer = customerNameField.getValue();

            if (selectedCustomer != null && !selectedCustomer.trim().isEmpty() && !selectedCustomer.equals("Cash Customer")) {
                invoice.setCustomerName(selectedCustomer);
                try {
                    long customerId = customerModel.getCustomerIdByName(selectedCustomer);
                    invoice.setCustomerId(customerId);
                } catch (SQLException e) {
                    e.printStackTrace();
                    invoice.setCustomerId(0);
                }
            } else {
                invoice.setCustomerName("Cash Customer");
                invoice.setCustomerId(1);
            }

            invoice.setTotalAmount(grandTotal);
            invoice.setPaidAmount(paid);

            invoice.setCashierId(getCurrentUserId());

            String paymentMethod = paymentMethodCombo.getValue();
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                paymentMethod = "Cash";
            }
            invoice.setPaymentMethod(paymentMethod);
            invoice.setDetails(cartList);

            long savedInvoiceId = invoiceModel.saveInvoiceWithTransaction(invoice);

            if (savedInvoiceId != -1) {
                this.lastSavedInvoiceId = savedInvoiceId;
                showAlert("Success", "Invoice #" + savedInvoiceId + " saved successfully!");
                printBill(savedInvoiceId, discountValue);

                clearAll();
            } else {
                showAlert("Failed", "Transaction failed! Please check stock availability.");
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric values for payment!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearLineFields();
    }

    @FXML
    private void handleResetForm() {
        clearAll();
    }

    private void clearAll() {
        cartList.clear();
        customerNameField.setValue(null);
        discountPercentField.clear();
        paidAmountField.clear();
        paymentMethodCombo.setValue("Cash");
        clearLineFields();
        updateSummary();
        loadInvoiceNumber();
    }

    private void clearLineFields() {
        productCombo.setValue(null);
        batchCombo.setValue(null);
        availableQtyField.setText("0");
        sellingPriceField.setText("0.00");
        lineQtyField.clear();
    }

    private void clearBatchDetails() {
        availableQtyField.setText("0");
        sellingPriceField.setText("0.00");
        selectedBatchId = 0;
        selectedSellingPrice = 0.0;
    }

    private boolean validateLine() {
        if (batchCombo.getValue() == null || lineQtyField.getText().trim().isEmpty()) {
            showAlert("Required", "Select batch and enter quantity!");
            return false;
        }

        try {
            int qty = Integer.parseInt(lineQtyField.getText().trim());
            if (qty <= 0) {
                showAlert("Invalid", "Quantity must be greater than 0!");
                return false;
            }

            String selectedDisplayText = batchCombo.getValue().toString();

            BatchDTO selectedBatch = batchModel.getBatchByDisplay(selectedDisplayText);

            if (selectedBatch != null) {
                LocalDate today = LocalDate.now();
                LocalDate expiryDate = selectedBatch.getExpiryDate();

                if (expiryDate.isBefore(today) || expiryDate.isEqual(today)) {
                    showAlert("Expired Medicine", "This batch (" + selectedBatch.getBatchNumber()
                            + ") expired on " + expiryDate + ". You cannot sell this!");
                    return false;
                }

                if (qty > selectedBatch.getQtyRemaining()) {
                    showAlert("Out of Stock", "Only " + selectedBatch.getQtyRemaining() + " units available in this batch!");
                    return false;
                }
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid", "Quantity must be a valid number!");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error while checking batch details!");
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private long getCurrentUserId() {
        if (SessionManager.currentUser != null) {
            return SessionManager.currentUser.getUserId();
        }
        return 1;
    }

    @FXML
    private void handlePrintButtonAction() {
        if (lastSavedInvoiceId == -1) {
            new Alert(Alert.AlertType.WARNING, "Please save the invoice before printing!").show();
            return;
        }

        try {
            String rawText = discountAmountLabel.getText().replace("Rs. ", "").replace(",", "").trim();

            if (rawText.indexOf('.') != rawText.lastIndexOf('.')) {
                int firstDot = rawText.indexOf('.');
                String part1 = rawText.substring(0, firstDot + 1);
                String part2 = rawText.substring(firstDot + 1).replace(".", "");
                rawText = part1 + part2;
            }

            BigDecimal discountValue;
            try {
                discountValue = rawText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(rawText);
            } catch (NumberFormatException e) {
                System.err.println("Invalid discount format, using 0 instead.");
                discountValue = BigDecimal.ZERO;
            }

            printBill(lastSavedInvoiceId, discountValue);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Print error: " + e.getMessage()).show();
        }
    }

    public void printBill(long invoiceId, BigDecimal discountValue) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            InputStream inputStream = getClass().getResourceAsStream("/lk/ijse/pharmacymanagmentsystem/report/Invoice.jrxml");

            if (inputStream == null) {
                System.out.println("Report file not found!");
                return;
            }
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("invoiceId", invoiceId);
            parameters.put("Discount", discountValue);

            JasperReport jr = JasperCompileManager.compileReport(inputStream);
            JasperPrint jp = JasperFillManager.fillReport(jr, parameters, conn);

            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Pharmacy Management System - Invoice #" + invoiceId);
            viewer.setVisible(true);
            viewer.toFront();

        } catch (SQLException | JRException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewLastInvoice() {
        if (lastSavedInvoiceId == -1) {
            showAlert("No Invoice", "No invoice has been saved in this session yet!");
            return;
        }

        try {

            String rawText = discountAmountLabel.getText().replace("Rs. ", "").replace(",", "").trim();
            java.math.BigDecimal discountValue = rawText.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(rawText);

            printBill(lastSavedInvoiceId, discountValue);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open the last invoice: " + e.getMessage());
        }
    }
}
