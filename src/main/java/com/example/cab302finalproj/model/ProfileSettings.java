package com.example.cab302finalproj.model;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ProfileSettings {
    @FXML
    private AnchorPane ProfileSettingsContent;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

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
    private void saveChanges() {
        String newEmail = emailField.getText().trim().toLowerCase(); // Normalize to lowercase
        String newPassword = passwordField.getText().trim();

        if (newEmail.isEmpty() || newPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Email and password cannot be empty.");
            return;
        }

        int userId = CurrentUser.getCurrentUserId();
        Connection conn = DatabaseManager.getInstance().getConnection();

        try {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!newEmail.matches(emailRegex)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid email address.");
                return;
            }

            if (!isDomainValid(newEmail)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error",
                        "Please enter an email address with a supported domain (gmail.com, outlook.com, yahoo.com, qut.edu.au).");
                return;
            }

            String checkEmailSql = "SELECT id FROM users WHERE email = ? AND id != ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkEmailSql)) {
                checkStmt.setString(1, newEmail);
                checkStmt.setInt(2, userId);
                if (checkStmt.executeQuery().next()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error",
                            "This email is already registered. Please use a different email.");
                    return;
                }
            }

            // Update email only
            if (newPassword.isEmpty() || newPassword.matches("\\*+")) {
                String sqlEmailOnly = "UPDATE users SET email = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlEmailOnly)) {
                    pstmt.setString(1, newEmail);
                    pstmt.setInt(2, userId);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Email updated successfully!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Update Error", "Failed to update email.");
                    }
                }
            } else {
                // Update email and password
                String hashedPassword = UserDAO.hashPassword(newPassword);
                String sqlFull = "UPDATE users SET email = ?, password = ? WHERE id = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sqlFull)) {
                    pstmt.setString(1, newEmail);
                    pstmt.setString(2, hashedPassword);
                    pstmt.setInt(3, userId);
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Update Error", "Failed to update profile.");
                    }
                }
            }


        } catch (SQLException | NoSuchAlgorithmException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update profile: " + e.getMessage());
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cab302finalproj/Settings.fxml"));
            AnchorPane settingsPane = loader.load();
            ProfileSettingsContent.getScene().setRoot(settingsPane);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to return to settings: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}