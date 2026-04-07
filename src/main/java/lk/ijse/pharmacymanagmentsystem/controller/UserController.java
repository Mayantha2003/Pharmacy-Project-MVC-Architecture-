package lk.ijse.pharmacymanagmentsystem.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.pharmacymanagmentsystem.dto.UserDTO;
import lk.ijse.pharmacymanagmentsystem.model.UserModel;

import java.net.URL;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class UserController implements Initializable {

    @FXML
    private TextField user_ID;

    @FXML
    private TextField user_Name;

    @FXML
    private TextField user_Contact;

    @FXML
    private TextField user_Password;

    @FXML
    private TextField user_FullName;

    @FXML
    private ComboBox<String> user_Role;

    @FXML
    private TableView user_Table;

    @FXML
    private TableColumn user_Id_Column;

    @FXML
    private TableColumn user_FullName_Column;

    @FXML
    private TableColumn user_Name_Column;

    @FXML
    private TableColumn user_Password_Column;

    @FXML
    private TableColumn user_Role_Column;

    @FXML
    private TableColumn user_Contact_Column;

    @FXML
    private TableColumn<UserDTO, Void> ActionCol;

    private final String USER_ID_REGEX = "^[0-9]+$";
    private final String USER_NAME_REGEX = "^[A-Za-z\\s]{3,}$";
    private final String USER_CONTACT_REGEX = "^(\\+94|0)[0-9]{9}$";
    private final String USER_PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
    private final String USER_FULLNAME_REGEX = "^[A-Za-z\\s.]{3,50}$";

    private final UserModel userModel = new UserModel();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        user_Role.setItems(FXCollections.observableArrayList("Owner", "Employee"));

        user_Id_Column.setCellValueFactory(new PropertyValueFactory<>("userId"));
        user_Name_Column.setCellValueFactory(new PropertyValueFactory<>("username"));
        user_Password_Column.setCellValueFactory(new PropertyValueFactory<>("password"));
        user_Contact_Column.setCellValueFactory(new PropertyValueFactory<>("contact"));
        user_Role_Column.setCellValueFactory(new PropertyValueFactory<>("role"));
        user_FullName_Column.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        user_Table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setDataToFields((UserDTO) newValue);
            }
        });

        setupUserActionsColumn();

        loadAllUsers();
    }

    private void setDataToFields(UserDTO user) {
        user_ID.setText(String.valueOf(user.getUserId()));
        user_ID.setEditable(false); 
        user_Name.setText(user.getUsername());
        user_Password.setText(user.getPassword());
        user_Contact.setText(user.getContact());
        user_FullName.setText(user.getFullName());
        user_Role.setValue(user.getRole()); 
    }

    private void setupUserActionsColumn() {
        ActionCol.setCellFactory(param -> new TableCell<UserDTO, Void>() {

            private final Button removeButton = new Button("🗑 Remove");

            {
                removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                        + "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");

                removeButton.setOnAction(event -> {
                    UserDTO selectedUser = getTableView().getItems().get(getIndex());

                    deleteUserLogic(selectedUser);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });
    }

    private void deleteUserLogic(UserDTO user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete User: " + user.getUsername());
        confirm.setContentText("Are you sure you want to remove this user permanently?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean result = userModel.deleteUser(user.getUserId());

                if (result) {
                    new Alert(Alert.AlertType.INFORMATION, "Success User deleted successfully!").show();
                    loadAllUsers();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed Could not delete user.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error Something went wrong: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSaveUser() {

        try {
            String userName = user_Name.getText().trim();
            String password = user_Password.getText();
            String contact = user_Contact.getText().trim();
            String role = user_Role.getValue();
            String fullName = user_FullName.getText().trim();

            if (userName.isEmpty() || !userName.matches(USER_NAME_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid User Name!").show();

            } else if (password.isEmpty() || !password.matches(USER_PASSWORD_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Password!").show();

            } else if (contact.isEmpty() || !contact.matches(USER_CONTACT_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Contact!").show();

            } else if (fullName.isEmpty() || !fullName.matches(USER_FULLNAME_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Full Name !").show();

            } else if (role == null || role.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please select a Role!").show();

            } else {

                UserDTO userDTO = new UserDTO(userName, password, contact, role, fullName);

                boolean result = userModel.saveUser(userDTO);

                if (result) {
                    new Alert(Alert.AlertType.INFORMATION, "User Added Successfully!").show();
                    loadAllUsers();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to add user! (Username may already exist)").show();
                }

            }
        } catch (SQLIntegrityConstraintViolationException e) {

            if (e.getMessage().contains("contact_number")) {
                new Alert(Alert.AlertType.ERROR,
                        "This Contact Number already exists!\nPlease use a different contact number.").show();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Duplicate entry detected!\nThis User already exists.").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchUser(KeyEvent event) {

        try {

            if (event.getCode() == KeyCode.ENTER) {
                String id = user_ID.getText().trim();

                if (id == null || id.trim().isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Please enter Customer ID!").show();

                } else {
                    UserDTO userDTO = userModel.searchUser(Long.parseLong(id));

                    if (userDTO != null) {

                        user_ID.setText(String.valueOf(userDTO.getUserId()));
                        user_ID.setEditable(false);
                        user_Name.setText(userDTO.getUsername());
                        user_Password.setText(userDTO.getPassword());
                        user_Contact.setText(userDTO.getContact());
                        user_Role.setValue(userDTO.getRole());
                        user_FullName.setText(userDTO.getFullName());
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "User Not Found!").show();
                    }

                }

            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Invalid ID!").show();
        }
    }

    @FXML
    private void handleUpdateUser() {

        try {

            String userid = user_ID.getText().trim();
            String userName = user_Name.getText().trim();
            String password = user_Password.getText();
            String contact = user_Contact.getText().trim();
            String role = user_Role.getValue();
            String fullName = user_FullName.getText().trim();

            if (userid.isEmpty() || !userid.matches(USER_ID_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid User ID!").show();

            } else if (userName.isEmpty() || !userName.matches(USER_NAME_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid User Name!").show();

            } else if (password.isEmpty() || !password.matches(USER_PASSWORD_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Password!").show();

            } else if (contact.isEmpty() || !contact.matches(USER_CONTACT_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Contact!").show();

            } else if (fullName.isEmpty() || !fullName.matches(USER_FULLNAME_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Full Name !").show();

            } else if (role == null || role.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please select a Role!").show();

            } else {

                UserDTO userDTO = new UserDTO(Long.parseLong(userid), userName, password, contact, role, fullName);

                boolean result = userModel.updateUser(userDTO);

                if (result) {
                    new Alert(Alert.AlertType.INFORMATION, "User Updated Successfully!").show();
                    loadAllUsers();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Update failed!").show();
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {

            if (e.getMessage().contains("contact")) {
                new Alert(Alert.AlertType.ERROR,
                        "This Contact Number already exists!\nPlease use a different contact number.").show();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Duplicate entry detected!\nThis User already exists.").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    private void loadAllUsers() {

        try {
            List<UserDTO> userList = userModel.getAllUsers();

            ObservableList<UserDTO> obList = FXCollections.observableArrayList();

            for (UserDTO customerDTO : userList) {
                obList.add(customerDTO);
            }

            user_Table.setItems(obList);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load User!").show();
            e.printStackTrace();
        }
    }

    private void clearFields() {
        user_ID.clear();
        user_ID.setEditable(true);
        user_Name.clear();
        user_Password.clear();
        user_Contact.clear();
        user_FullName.clear();
        user_Role.setValue(null);
    }
}
