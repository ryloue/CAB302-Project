package com.example.cab302finalproj.model;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class
SignUp {
    public Hyperlink Login_Link;
    public AnchorPane SignUpContent;

    private static final Logger LOGGER = Logger.getLogger(SignUp.class.getName());

    @FXML
    public TextField emailField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public PasswordField confirmPasswordField;

    // List of valid domains
    private final List<String> validDomains = Arrays.asList(
            "@gmail.com",
            "@outlook.com",
            "@yahoo.com",
            "@qut.edu.au"
    );

    private boolean isDomainValid(String email) {
        for (String domain : validDomains) {
            if (email.toLowerCase().endsWith(domain)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void GotoLogin() {
        try {
            AnchorPane newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cab302finalproj/Login.fxml")));
            SignUpContent.getChildren().setAll(newPage);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading Login FXML.", e);
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        try {
            if (!validateInputs()) {
                return;
            }

            String email = emailField.getText();
            String password = passwordField.getText();
            try {
                boolean success = UserDAO.registerUser(email, password);

                if (!success) {
                    showAlert(Alert.AlertType.ERROR, "Registration Error",
                            "Could not register the user. Please try again.");
                    return;
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("already exists")) {
                    showAlert(Alert.AlertType.ERROR, "Registration Error",
                            "This email is already registered. Please use a different email or log in.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Registration Error",
                            "Database error: " + e.getMessage());
                    LOGGER.log(Level.SEVERE, "Database error during registration", e);
                }
                return;
            } catch (NoSuchAlgorithmException e) {
                showAlert(Alert.AlertType.ERROR, "Registration Error",
                        "Security error: " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Security error during registration", e);
                return;
            }

            try {
                boolean loginSuccess = UserDAO.authenticateUser(email, password);
                if (!loginSuccess) {
                    showAlert(Alert.AlertType.ERROR, "Login Error",
                            "Failed to log in after registration. Please try logging in manually.");
                    return;
                }
            } catch (SQLException | NoSuchAlgorithmException e) {
                showAlert(Alert.AlertType.ERROR, "Login Error",
                        "Database error during login: " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Database error during auto-login", e);
                return;
            }

            showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                    "Your account has been created successfully! You are now logged in.");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cab302finalproj/MainLayout.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "An error occurred while navigating to the dashboard.");
            LOGGER.log(Level.SEVERE, "Error navigating to dashboard", e);
        }
    }

    public boolean validateInputs() {

        if (emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "All fields are required. Please fill in all fields.");
            return false;
        }


        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Pattern.matches(emailRegex, emailField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please enter a valid email address.");
            return false;
        }

        // Check if email ends with valid domains
        if (!isDomainValid(emailField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please enter a valid email address with one of our supported domains (gmail.com, outlook.com, yahoo.com, qut.edu.au)");
            return false;
        }

        if (passwordField.getText().length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Password must be at least 8 characters long.");
            return false;
        }


        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Passwords do not match. Please try again.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}