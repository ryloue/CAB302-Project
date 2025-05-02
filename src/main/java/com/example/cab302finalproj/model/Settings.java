package com.example.cab302finalproj.model;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Settings {
    @FXML
    private AnchorPane SettingsContent;

    @FXML
    private void changeTheme() {
        // Placeholder for theme change logic
    }

    @FXML
    private void notificationPreferences() {
        // Placeholder for notification preferences logic
    }

    @FXML
    private void resetNotes() {
        // Placeholder for reset notes logic
    }

    @FXML
    private void showProfileSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cab302finalproj/ProfileSettings.fxml"));
            AnchorPane profilePane = loader.load();
            SettingsContent.getChildren().setAll(profilePane);

            // Retrieve and set user data
            int userId = CurrentUser.getCurrentUserId();
            Connection conn = DatabaseManager.getInstance().getConnection();
            String sql = "SELECT email FROM users WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        TextField emailField = (TextField) profilePane.lookup("#emailField");
                        TextField passwordField = (TextField) profilePane.lookup("#passwordField");

                        emailField.setText(rs.getString("email"));
                        passwordField.setText("********"); // Masked password
                    }
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load profile data: " + e.getMessage());
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load profile settings page: " + e.getMessage());
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