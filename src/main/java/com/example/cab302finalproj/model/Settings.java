package com.example.cab302finalproj.model;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import net.bytebuddy.asm.MemberSubstitution;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Settings {


    @FXML
    private Pane ProfileSettingsContent;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;

    @FXML
    private void resetNotes() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset Notes");
        confirm.setHeaderText("Are you sure you want to reset your notes");
        confirm.setContentText("This action cannot be undone");

        Optional<ButtonType> result = confirm.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            performNoteReset();
            showAlert(Alert.AlertType.INFORMATION, "Reset Successful", "Your Notes have been reset");
        }else{
            showAlert(Alert.AlertType.INFORMATION, "Cancelled","Note reset was cancelled");
        }
    }

    private void performNoteReset(){
        int userId = CurrentUser.getCurrentUserId();
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM DashNotes where userId = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Notes Successfully deleted from User ID: " + userId);
            } else {
                System.out.println("No notes found to delete from userId: " + userId);
            }
        }catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Data Error", "Failed to reset notes: " + e.getMessage());
        }
    }

    @FXML
    private void showProfileSettings() {
        ProfileSettingsContent.setVisible(true);


        // Retrieve and set user data
        int userId = CurrentUser.getCurrentUserId();
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT email FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    TextField emailField = (TextField) ProfileSettingsContent.lookup("#emailField");
                    TextField passwordField = (TextField) ProfileSettingsContent.lookup("#passwordField");

                    emailField.setText(rs.getString("email"));
                    passwordField.setText("********"); // Masked password
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load profile data: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
        ProfileSettingsContent.setVisible(false);
    }


}
