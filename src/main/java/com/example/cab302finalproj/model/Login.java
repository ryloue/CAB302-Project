package com.example.cab302finalproj.model;

import com.example.cab302finalproj.controller.ForgotPassword;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;

import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;


public class Login {

    private static final Logger LOGGER = Logger.getLogger(Login.class.getName());

    @FXML
    private AnchorPane LoginContent;

    @FXML
    public TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private CheckBox showPasswordCB;

    @FXML
    private void handleForgotPassword() {
        try {
            AnchorPane newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cab302finalproj/ForgotPassword.fxml")));
            LoginContent.getChildren().setAll(newPage);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading ForgotPassword FXML", e);
        }
    }

    @FXML
    public void initialize(){
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        passwordVisibleField.managedProperty().bind(passwordVisibleField.visibleProperty());
        passwordVisibleField.setStyle(passwordField.getStyle());
    }

    @FXML
    private void handleShowPassword(ActionEvent event){
        boolean show = showPasswordCB.isSelected();
        passwordVisibleField.setVisible(show);
        passwordField.setVisible(!show);
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        try {
            // Get the email and password
            String email = emailField.getText();
            String password = passwordField.getText();

            // Validate inputs
            if (email.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Login Error",
                        "Please enter both email and password.");
                return;
            }

            try {
                boolean loginSuccess = UserDAO.authenticateUser(email, password);

                if (!loginSuccess) {
                    showAlert(Alert.AlertType.ERROR, "Login Error",
                            "Invalid email or password. Please try again.");
                    return;
                }
            } catch (SQLException | NoSuchAlgorithmException e) {
                showAlert(Alert.AlertType.ERROR, "Login Error",
                        "Database error: " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Database error during login", e);
                return;
            }

            // Login successful, navigate to dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cab302finalproj/MainLayout.fxml"));
            Parent root = loader.load();

            // SAFE WAY TO GET THE STAGE:
            Stage stage;
            if (event.getSource() instanceof Node) {
                // If triggered by button click
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                // If triggered programmatically or by keyboard
                stage = (Stage) emailField.getScene().getWindow(); // Use any existing node
            }

            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login Error",
                    "An error occurred during login. Please try again.");
            LOGGER.log(Level.SEVERE, "Error during login", e);
        }
    }

    @FXML
    private void GotoSignUp() {
        try {
            AnchorPane newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cab302finalproj/SignUp.fxml")));
            LoginContent.getChildren().setAll(newPage);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading SignUp FXML", e);
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean validateLogin(String email, String password) {
        // Check if email or password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both email and password.");
            return false;
        }

        // Validate email format (basic validation)
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid email format. Please enter a valid email.");
            return false;
        }

        // If both email and password are valid
        return true;
    }
}
