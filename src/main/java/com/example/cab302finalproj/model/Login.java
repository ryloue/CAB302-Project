package com.example.cab302finalproj.model;

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
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleForgotPassword() {
        showAlert(Alert.AlertType.INFORMATION, "Forgot Password",
                "Password reset is not implemented yet. Please contact support.");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
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

            // Authenticate user using SQLite database
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

            // This gets the current stage from the event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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

    /**
     * Shows an alert dialog
     * @param type The alert type (error, information, etc.)
     * @param title The alert title
     * @param message The alert message
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}