package lk.ijse.pharmacymanagmentsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.pharmacymanagmentsystem.dto.ProductDTO;
import lk.ijse.pharmacymanagmentsystem.model.CategorieModel;
import lk.ijse.pharmacymanagmentsystem.model.ProductModel;

import java.net.URL;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private TextField codeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField packSizeField;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private CheckBox medicineCheckBox;
    @FXML
    private TextField genericNameField;
    @FXML
    private TextField strengthField;
    @FXML
    private ComboBox<String> dosageFormCombo;
    @FXML
    private TextField companyField;

    @FXML
    private TableView<ProductDTO> productTable;
    @FXML
    private TableColumn idCol;
    @FXML
    private TableColumn codeCol;
    @FXML
    private TableColumn nameCol;
    @FXML
    private TableColumn packCol;
    @FXML
    private TableColumn<ProductDTO, String> catCol;
    @FXML
    private TableColumn<ProductDTO, Void> actionsCol;

    private final String CODE_REGEX = "^[A-Z0-9-]{3,20}$";
    private final String NAME_REGEX = "^[A-Za-z0-9\\s(),-]{3,100}$";
    private final String PACK_REGEX = "^[A-Za-z0-9\\s xX-]{3,50}$";

    private final ProductModel productModel = new ProductModel();
    private final CategorieModel categorieModel = new CategorieModel();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Product FXML is Loaded");

        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        packCol.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        catCol.setCellValueFactory(cellData -> {
            try {
                String display = categorieModel.getCategoryDisplayName(cellData.getValue().getCatId());
                return new javafx.beans.property.SimpleStringProperty(display);
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            }
        });

        productTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setDataToFields(newValue);
            }
        });

        loadCategories();
        setupActionsColumn();
        loadDosageForms();
        loadProductTable();
        setupMedicineControl();
    }

    private void setupActionsColumn() {
        actionsCol.setCellFactory(param -> new TableCell<ProductDTO, Void>() {
            private final Button btnRemove = new Button("🗑 Remove");

            {
                btnRemove.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                        + "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");

                btnRemove.setOnAction(event -> {
                    ProductDTO selectedProduct = getTableView().getItems().get(getIndex());
                    handleDeleteAction(selectedProduct);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnRemove);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });
    }

    private void handleDeleteAction(ProductDTO product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete: " + product.getName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                boolean isDeleted = productModel.deleteProduct(product.getProductId());

                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Product removed successfully!");
                    loadProductTable();
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete product.");
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                showAlert(Alert.AlertType.ERROR, "Cannot Delete",
                        "This product cannot be deleted because it has associated Batch data. Please remove the batches first.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "DB Error", "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadCategories() {
        try {
            categoryCombo.setItems(categorieModel.getAllCategoriesForCombo());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load categories!");
        }
    }

    private void loadDosageForms() {
        dosageFormCombo.getItems().addAll("Tablet", "Capsule", "Syrup", "Injection", "Cream", "Gel", "Ointment");
    }

    private void setupMedicineControl() {
        categoryCombo.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                try {
                    String type = categorieModel.getCatTypeFromDisplay(newVal);
                    boolean isMedicine = "Medicine".equals(type);
                    medicineCheckBox.setSelected(isMedicine);
                    setMedicineFieldsEnabled(isMedicine);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setMedicineFieldsEnabled(boolean enabled) {
        genericNameField.setDisable(!enabled);
        strengthField.setDisable(!enabled);
        dosageFormCombo.setDisable(!enabled);
        companyField.setDisable(!enabled);

        if (!enabled) {
            genericNameField.clear();
            strengthField.clear();
            dosageFormCombo.getSelectionModel().clearSelection();
            companyField.clear();
        }
    }

    @FXML
    private void handleSaveProduct() {
        if (!validateInput(false)) {
            return;
        }

        try {
            long catId = categorieModel.getCatIdFromDisplay(categoryCombo.getValue());

            ProductDTO dto = new ProductDTO(
                    codeField.getText().trim(),
                    nameField.getText().trim(),
                    packSizeField.getText().trim(),
                    catId,
                    medicineCheckBox.isSelected() ? genericNameField.getText().trim() : null,
                    medicineCheckBox.isSelected() ? strengthField.getText().trim() : null,
                    medicineCheckBox.isSelected() ? dosageFormCombo.getValue() : null,
                    medicineCheckBox.isSelected() ? companyField.getText().trim() : null
            );

            boolean result = productModel.saveProduct(dto);
            if (result) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product saved successfully!");
                loadProductTable();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Failed to save product!");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplicate", "Product code already exists!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateProduct() {
        ProductDTO selectedProduct = productTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product from the table to update!");
            return;
        }

        if (!validateInput(true)) {
            return;
        }

        try {
            long productId = selectedProduct.getProductId();
            long catId = categorieModel.getCatIdFromDisplay(categoryCombo.getValue());

            ProductDTO dto = new ProductDTO(
                    productId,
                    codeField.getText().trim(),
                    nameField.getText().trim(),
                    packSizeField.getText().trim(),
                    catId,
                    medicineCheckBox.isSelected() ? genericNameField.getText().trim() : null,
                    medicineCheckBox.isSelected() ? strengthField.getText().trim() : null,
                    medicineCheckBox.isSelected() ? dosageFormCombo.getValue() : null,
                    medicineCheckBox.isSelected() ? companyField.getText().trim() : null
            );

            boolean result = productModel.updateProduct(dto);
            if (result) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");
                loadProductTable();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Update failed! Please check database connectivity.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchProduct(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String searchText = searchField.getText().trim();

            if (searchText.isEmpty()) {
                loadProductTable();
                return;
            }

            try {
                ProductDTO dto = productModel.searchExactProduct(searchText);

                if (dto != null) {
                    setDataToFields(dto);

                    ObservableList<ProductDTO> obList = FXCollections.observableArrayList(dto);
                    productTable.setItems(obList);

                } else {
                    showAlert(Alert.AlertType.ERROR, "Not Found", "No product found with ID or Code: " + searchText);
                    clearFields();
                    loadProductTable();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error while searching!");
            }
        }
    }

    @FXML
    private void handleReset() {
        clearFields();
    }

    private void loadProductTable() {
        try {
            List<ProductDTO> list = productModel.getAllProducts();
            ObservableList<ProductDTO> obList = FXCollections.observableArrayList(list);
            productTable.setItems(obList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load products!");
        }
    }

    private boolean validateInput(boolean isUpdate) {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String category = categoryCombo.getValue();

        if (code.isEmpty() || !code.matches(CODE_REGEX)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Code", "Product code must be 3-20 uppercase letters/numbers!");
            return false;
        }
        if (name.isEmpty() || !name.matches(NAME_REGEX)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Name", "Product name invalid!");
            return false;
        }
        if (category == null) {
            showAlert(Alert.AlertType.ERROR, "Required", "Please select a category!");
            return false;
        }

        if (medicineCheckBox.isSelected()) {
            if (genericNameField.getText().trim().isEmpty() || strengthField.getText().trim().isEmpty() || dosageFormCombo.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Required for Medicine", "Generic Name, Strength and Dosage Form required for medicines!");
                return false;
            }
        }
        return true;
    }

    private void clearFields() {
        codeField.clear();
        nameField.clear();
        packSizeField.clear();
        categoryCombo.getSelectionModel().clearSelection();
        medicineCheckBox.setSelected(false);
        genericNameField.clear();
        strengthField.clear();
        dosageFormCombo.getSelectionModel().clearSelection();
        companyField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setDataToFields(ProductDTO product) {
        try {
            codeField.setText(product.getCode());
            nameField.setText(product.getName());
            packSizeField.setText(product.getPackSize());

            String categoryDisplay = categorieModel.getCategoryDisplayName(product.getCatId());
            categoryCombo.setValue(categoryDisplay);

            if (product.getGenericName() != null && !product.getGenericName().isEmpty()) {
                medicineCheckBox.setSelected(true);
                setMedicineFieldsEnabled(true);
                genericNameField.setText(product.getGenericName());
                strengthField.setText(product.getStrength());
                dosageFormCombo.setValue(product.getDosageForm());
                companyField.setText(product.getCompany());
            } else {
                medicineCheckBox.setSelected(false);
                setMedicineFieldsEnabled(false);
                genericNameField.clear();
                strengthField.clear();
                dosageFormCombo.getSelectionModel().clearSelection();
                companyField.clear();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load product details properly!");
            e.printStackTrace();
        }
    }
}
