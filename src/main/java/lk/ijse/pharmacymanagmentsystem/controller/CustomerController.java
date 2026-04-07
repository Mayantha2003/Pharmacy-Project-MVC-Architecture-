package lk.ijse.pharmacymanagmentsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.pharmacymanagmentsystem.dto.CustomerDTO;
import lk.ijse.pharmacymanagmentsystem.model.CustomerModel;

import java.net.URL;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

        @FXML
        private TextField customer_ID;
        @FXML
        private TextField customer_Name;
        @FXML
        private TextField customer_Address;
        @FXML
        private TextField customer_Contact;
        @FXML
        private TextField customer_Email;
    
        @FXML
        private TableColumn cus_Id_Column;
        @FXML
        private TableColumn cus_Name_Column;
        @FXML
        private TableColumn cus_Address_Column;
        @FXML
        private TableColumn cus_Contact_Column;
        @FXML
        private TableColumn cus_Email_Column;
    
        @FXML
        private TableColumn<CustomerDTO, Void> ActionCol;
    
        @FXML
        private TableView table_Customer;
    
        private final String CUSTOMER_ID_REGEX = "^[0-9]+$";
        private final String CUSTOMER_NAME_REGEX = "^[A-Za-z\\s]{3,}$";
        private final String CUSTOMER_ADDRESS_REGEX = "^[A-Za-z0-9\\s,./#-]{5,}$";
        private final String CUSTOMER_CONTACT_REGEX = "^(\\+94|0)[0-9]{9}$";
        private final String CUSTOMER_EMAIL_REGEX
                = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*"
                + "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    
        private final CustomerModel customerModel = new CustomerModel();
    
        @Override
        public void initialize(URL url, ResourceBundle rb) {
            System.out.println("Customer FXML is Loaded");
    
            cus_Id_Column.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            cus_Name_Column.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            cus_Address_Column.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
            cus_Contact_Column.setCellValueFactory(new PropertyValueFactory<>("customerContactNumber"));
            cus_Email_Column.setCellValueFactory(new PropertyValueFactory<>("customerEmail"));
    
            table_Customer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    setDataToFields((CustomerDTO) newValue);
                }
            });
            ActionColumn();
            loadCustomerTable();
        }
    
        private void setDataToFields(CustomerDTO customer) {
            customer_ID.setText(String.valueOf(customer.getCustomerId()));
            customer_Name.setText(customer.getCustomerName());
            customer_Address.setText(customer.getCustomerAddress());
            customer_Contact.setText(customer.getCustomerContactNumber());
            customer_Email.setText(customer.getCustomerEmail());
        }
    
        private void ActionColumn() {
            ActionCol.setCellFactory(column -> {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("🗑 Remove");
    
                    {
                        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                                + "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
    
                        deleteButton.setOnAction(event -> {
                            CustomerDTO customer = getTableView().getItems().get(getIndex());
                            handleActionDelete(customer);
                        });
                    }
    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                            setAlignment(javafx.geometry.Pos.CENTER);
                        }
                    }
                };
            });
        }
    
        private void handleActionDelete(CustomerDTO customer) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete customer: " + customer.getCustomerName() + "?",
                    ButtonType.YES, ButtonType.NO);
    
            alert.setTitle("Delete Customer");
            alert.setHeaderText(null);
    
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        boolean isDeleted = customerModel.deleteCustomer(customer.getCustomerId());
                        if (isDeleted) {
                            new Alert(Alert.AlertType.INFORMATION, "Customer Deleted Successfully!").show();
                            loadCustomerTable();
                            cleanFields();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Failed to delete customer!").show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "An error occurred while deleting!").show();
                    }
                }
            });
        }
    
        @FXML
        private void handleSaveCustomer() {
            try {
                String name = customer_Name.getText().trim();
                String address = customer_Address.getText().trim();
                String contact = customer_Contact.getText().trim();
                String email = customer_Email.getText().trim();
    
                if (name.isEmpty() || !name.matches(CUSTOMER_NAME_REGEX)) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Customer Name! (Letters and spaces only, min 3 characters)").show();
    
                } else if (address.isEmpty() || !address.matches(CUSTOMER_ADDRESS_REGEX)) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Address! (Minimum 5 characters)").show();
    
                } else if (contact.isEmpty() || !contact.matches(CUSTOMER_CONTACT_REGEX)) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Contact Number! (e.g., 0712345678 or +94712345678)").show();
    
                } else if (!email.isEmpty() && !email.matches(CUSTOMER_EMAIL_REGEX)) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Email Address! (e.g., example@domain.com)").show();
    
                } else {
    
                    CustomerDTO customerDTO = new CustomerDTO(name, contact, address, email);
                    boolean result = customerModel.saveCustomer(customerDTO);
    
                    if (result) {
                        new Alert(Alert.AlertType.INFORMATION, "Customer Saved Successfully!").show();
                        loadCustomerTable();
                        cleanFields();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to save Customer!").show();
                    }
                }
    
            } catch (SQLIntegrityConstraintViolationException e) {
    
                if (e.getMessage().contains("contact")) {
                    new Alert(Alert.AlertType.ERROR,
                            "This Contact Number already exists!\nPlease use a different contact number.").show();
                } else {
                    new Alert(Alert.AlertType.ERROR,
                            "Duplicate entry detected!\nThis Customer already exists.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Something Went Wrong!").show();
                e.printStackTrace();
            }
        }
    
        @FXML
        private void handleSearchCustomer(KeyEvent event) {
            try {
                if (event.getCode() == KeyCode.ENTER) {
                    String id = customer_ID.getText().trim();
    
                    if (id == null || id.trim().isEmpty()) {
                        new Alert(Alert.AlertType.WARNING, "Please enter Customer ID!").show();
    
                    } else {
                        CustomerDTO customerDTO = customerModel.searchCustomer(Long.parseLong(id));
    
                        if (customerDTO != null) {
                            customer_ID.setText(String.valueOf(customerDTO.getCustomerId()));
                            customer_Name.setText(customerDTO.getCustomerName());
                            customer_Address.setText(customerDTO.getCustomerAddress());
                            customer_Contact.setText(customerDTO.getCustomerContactNumber());
                            customer_Email.setText(customerDTO.getCustomerEmail());
                        } else {
                            new Alert(Alert.AlertType.INFORMATION, "Customer Not Found!").show();
                        }
    
                    }
    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        @FXML
        private void handleCustomerUpdate() {
            try {
                String id = customer_ID.getText().trim();
                String name = customer_Name.getText().trim();
                String address = customer_Address.getText().trim();
                String contact = customer_Contact.getText().trim();
                String email = customer_Email.getText().trim();
    
                if (id.isEmpty() || !id.matches(CUSTOMER_ID_REGEX)) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Customer ID!").show();
    
                } else if (name.isEmpty() || !name.matches(CUSTOMER_NAME_REGEX)) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Customer Name!").show();
    
                } else if (address.isEmpty() || !address.matches(CUSTOMER_ADDRESS_REGEX)) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Address!").show();
    
                } else if (contact.isEmpty() || !contact.matches(CUSTOMER_CONTACT_REGEX)) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Contact Number!").show();
    
                } else if (!email.isEmpty() && !email.matches(CUSTOMER_EMAIL_REGEX)) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Email Address!").show();
    
                } else {
    
                    CustomerDTO customerDTO = new CustomerDTO(Long.parseLong(id), name, contact, address, email);
    
                    boolean result = customerModel.updateCustomer(customerDTO);
    
                    if (result) {
                        new Alert(Alert.AlertType.INFORMATION, "Customer Updated Successfully!").show();
                        loadCustomerTable();
                        cleanFields();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update Customer!").show();
                    }
                }
            } catch (SQLIntegrityConstraintViolationException e) {
    
                if (e.getMessage().contains("contact_number")) {
                    new Alert(Alert.AlertType.ERROR,
                            "This Contact Number already exists!\nPlease use a different contact number.").show();
                } else {
                    new Alert(Alert.AlertType.ERROR,
                            "Duplicate entry detected!\nThis Customer already exists.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Something Went Wrong!").show();
                e.printStackTrace();
            }
        }
    
        @FXML
        private void loadCustomerTable() {
            try {
                List<CustomerDTO> customerList = customerModel.getAllCustomers();
    
                ObservableList<CustomerDTO> obList = FXCollections.observableArrayList();
    
                for (CustomerDTO customerDTO : customerList) {
                    obList.add(customerDTO);
                }
    
                table_Customer.setItems(obList);
    
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Failed to load customers!").show();
                e.printStackTrace();
            }
        }
    
        private void cleanFields() {
            customer_ID.setText("");
            customer_Name.setText("");
            customer_Address.setText("");
            customer_Contact.setText("");
            customer_Email.setText("");
        }

}
