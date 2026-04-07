package lk.ijse.pharmacymanagmentsystem.controller;

import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.pharmacymanagmentsystem.App;
import lk.ijse.pharmacymanagmentsystem.dto.UserDTO;
import lk.ijse.pharmacymanagmentsystem.model.UserModel;
import lk.ijse.pharmacymanagmentsystem.util.SessionManager;

public class LoginController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private CheckBox showPasswordCheckBox;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    private final UserModel userModel = new UserModel();

    @FXML
    private void initialize() {
        setupPasswordVisibility();
        errorLabel.setVisible(false);
        usernameField.requestFocus();
    }

    private void setupPasswordVisibility() {
        visiblePasswordField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        visiblePasswordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordField.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void login() {
        String username = usernameField.getText().trim();
        String password = showPasswordCheckBox.isSelected() ? visiblePasswordField.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and Password are required!");
            return;
        }

        try {
            UserDTO user = userModel.login(username, password);

            if (user != null) {
                
                SessionManager.currentUser = user;

                errorLabel.setVisible(false);

                try {
                    App.setRoot("AdminLayout");
                } catch (IOException e) {
                    showError("Failed to load System Layout!");
                }
            } else {
                showError("Incorrect Username or Password!");
                shakeAnimation();
            }
        } catch (SQLException e) {
            showError("Database error!");
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void shakeAnimation() {
        javafx.animation.TranslateTransition shake = new javafx.animation.TranslateTransition(Duration.millis(50), rootPane);
        shake.setFromX(0);
        shake.setByX(12);
        shake.setCycleCount(8);
        shake.setAutoReverse(true);
        shake.play();
    }

    @FXML
    private void handleEnterPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            login();
        }
    }

}
