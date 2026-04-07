package lk.ijse.pharmacymanagmentsystem.controller;

import java.net.URL;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.pharmacymanagmentsystem.dto.SupplierDTO;
import lk.ijse.pharmacymanagmentsystem.model.SupplierModel;

public class SupplierController implements Initializable {

    @FXML
    private TextField sup_ID;

    @FXML
    private TextField sup_Name;

    @FXML
    private TextField sup_Address;

    @FXML
    private TextField sup_Contact;

    @FXML
    private TextField sup_Email;

    @FXML
    private TableColumn sup_Address_Column;

    @FXML
    private TableColumn sup_Contact_Column;

    @FXML
    private TableColumn sup_Email_Column;

    @FXML
    private TableColumn sup_Id_Column;

    @FXML
    private TableColumn sup_Name_Column;

    @FXML
    private TableColumn<SupplierDTO, Void> ActionCol;

    @FXML
    private TableView table_Supplier;

    private final String SUPPLIER_ID_REGEX = "^[0-9]+$";
    private final String SUPPLIER_NAME_REGEX = "^[A-Za-z\\s]{3,}$";
    private final String SUPPLIER_ADDRESS_REGEX = "^[A-Za-z0-9\\s,./#-]{5,}$";
    private final String SUPPLIER_CONTACT_REGEX = "^(\\+94|0)[0-9]{9}$";
    private final String SUPPLIER_EMAIL_REGEX
            = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*"
            + "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

    private final SupplierModel supplierModel = new SupplierModel();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        System.out.println("Supplier FXML is Loaded");

        sup_Id_Column.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        sup_Name_Column.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        sup_Address_Column.setCellValueFactory(new PropertyValueFactory<>("supplierAddress"));
        sup_Contact_Column.setCellValueFactory(new PropertyValueFactory<>("supplierContactNumber"));
        sup_Email_Column.setCellValueFactory(new PropertyValueFactory<>("supplierEmail"));

        table_Supplier.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setDataToFields((SupplierDTO) newValue);
            }
        });

        setupSupplierActionColumn();
        loadSupplierTable();

    }

    private void setDataToFields(SupplierDTO supplier) {
        sup_ID.setText(String.valueOf(supplier.getSupplierId()));
        sup_ID.setEditable(false); 
        sup_Name.setText(supplier.getSupplierName());
        sup_Address.setText(supplier.getSupplierAddress());
        sup_Contact.setText(supplier.getSupplierContactNumber());
        sup_Email.setText(supplier.getSupplierEmail());
    }

    private void setupSupplierActionColumn() {
        ActionCol.setCellFactory(param -> new TableCell<SupplierDTO, Void>() {
            private final Button btnDelete = new Button("🗑 Remove");

            {
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                        + "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");

                btnDelete.setOnAction(event -> {
                    SupplierDTO selectedSup = getTableView().getItems().get(getIndex());
                    handleDeleteAction(selectedSup);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });
    }

    private void handleDeleteAction(SupplierDTO supplier) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete Supplier " + supplier.getSupplierName() + "?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                boolean result = supplierModel.deleteSupplier(supplier.getSupplierId());
                if (result) {
                    new Alert(Alert.AlertType.INFORMATION, "Deleted!").show();
                    loadSupplierTable();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error deleting: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    private void handleSaveSupplier() {

        try {
            String name = sup_Name.getText().trim();
            String address = sup_Address.getText().trim();
            String contact = sup_Contact.getText().trim();
            String email = sup_Email.getText().trim();

            if (name.isEmpty() || !name.matches(SUPPLIER_NAME_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Supplier Name!").show();

            } else if (address.isEmpty() || !address.matches(SUPPLIER_ADDRESS_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Supplier Address!").show();

            } else if (contact.isEmpty() || !contact.matches(SUPPLIER_CONTACT_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Supplier Contact Number!").show();

            } else if (!email.isEmpty() && !email.matches(SUPPLIER_EMAIL_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Supplier Email Address!").show();

            } else {

                SupplierDTO supplierDTO = new SupplierDTO(name, address, contact, email);

                boolean result = supplierModel.saveSupplier(supplierDTO);

                if (result) {
                    new Alert(Alert.AlertType.INFORMATION, "Supplier Saved Successfully !").show();
                    loadSupplierTable();
                    cleanFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save Supplier!").show();
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {

            if (e.getMessage().contains("contact_number")) {
                new Alert(Alert.AlertType.ERROR,
                        "This Contact Number already exists!\nPlease use a different contact number.").show();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Duplicate entry detected!\nThis supplier already exists.").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something Went Wrong !").show();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchSupplier(KeyEvent event) {

        try {
            if (event.getCode() == KeyCode.ENTER) {
                String id = sup_ID.getText().trim();

                if (id == null || id.trim().isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Please enter Supplier ID!").show();
                } else {

                    SupplierDTO supplierDTO = supplierModel.searchSupplier(Long.parseLong(id));

                    if (supplierDTO != null) {

                        sup_ID.setText(String.valueOf(supplierDTO.getSupplierId()));
                        sup_Name.setText(supplierDTO.getSupplierName());
                        sup_Address.setText(supplierDTO.getSupplierAddress());
                        sup_Contact.setText(supplierDTO.getSupplierContactNumber());
                        sup_Email.setText(supplierDTO.getSupplierEmail());
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Supplier Not Found").show();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleSupplierUpdate() {

        try {

            String id = sup_ID.getText().trim();
            String name = sup_Name.getText().trim();
            String address = sup_Address.getText().trim();
            String contact = sup_Contact.getText().trim();
            String email = sup_Email.getText().trim();

            if (id.isEmpty() || !id.matches(SUPPLIER_ID_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Supplier ID!").show();

            } else if (name.isEmpty() || !name.matches(SUPPLIER_NAME_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Supplier Name!").show();

            } else if (address.isEmpty() || !address.matches(SUPPLIER_ADDRESS_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Supplier Address!").show();

            } else if (contact.isEmpty() || !contact.matches(SUPPLIER_CONTACT_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Supplier Contact Number!").show();

            } else if (!email.isEmpty() && !email.matches(SUPPLIER_EMAIL_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Supplier Email Address!").show();

            } else {

                SupplierDTO supplierDTO = new SupplierDTO(Long.parseLong(id), name, address, contact, email);

                boolean result = supplierModel.updateSupplier(supplierDTO);

                if (result) {
                    new Alert(Alert.AlertType.INFORMATION, "Supplier Updated Successfully !").show();
                    loadSupplierTable();
                    cleanFields();

                } else {
                    new Alert(Alert.AlertType.ERROR, "Something Went Wrong !").show();
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {

            if (e.getMessage().contains("contact_number")) {
                new Alert(Alert.AlertType.ERROR,
                        "This Contact Number already exists!\nPlease use a different contact number.").show();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Duplicate entry detected!\nThis supplier already exists.").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something Went Wrong !").show();
            e.printStackTrace();
        }
    }

    @FXML
    private void loadSupplierTable() {
        try {
            List<SupplierDTO> customerList = supplierModel.getAllSuppliers();

            ObservableList<SupplierDTO> obList = FXCollections.observableArrayList();

            for (SupplierDTO customerDTO : customerList) {
                obList.add(customerDTO);
            }

            table_Supplier.setItems(obList);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load Supplier!").show();
            e.printStackTrace();
        }
    }

    private void cleanFields() {
        sup_ID.setText("");
        sup_Name.setText("");
        sup_Address.setText("");
        sup_Contact.setText("");
        sup_Email.setText("");
    }

}
