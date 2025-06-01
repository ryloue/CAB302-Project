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

    /**
     * Prompts the user for confirmation and, if confirmed, resets all notes, flashcards,
     * and prompts for the current user. Shows a success or cancellation alert accordingly.
     */
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
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Note reset was cancelled");
        }
    }

    /**
     * Deletes all dashboard notes, flashcards, and prompts associated with the current user
     * in a single transaction. Rolls back if any delete fails and shows an error alert.
     */
    private void performNoteReset() {
        int userId = CurrentUser.getCurrentUserId();
        Connection conn = DatabaseManager.getInstance().getConnection();

        String deleteDashNotesSql = "DELETE FROM DashNotes WHERE userId = ?";
        String deleteFlashcardsSql = "DELETE FROM flashcards WHERE fromId = ?";
        String deletePromptsSql = "DELETE FROM prompts WHERE user_id = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(deleteDashNotesSql)) {
                ps1.setInt(1, userId);
                int dashNotesDeleted = ps1.executeUpdate();
                System.out.println(dashNotesDeleted > 0
                        ? "Deleted " + dashNotesDeleted + " DashNotes for user " + userId
                        : "No DashNotes to delete for user " + userId);
            }

            try (PreparedStatement ps2 = conn.prepareStatement(deleteFlashcardsSql)) {
                ps2.setInt(1, userId);
                int flashcardsDeleted = ps2.executeUpdate();
                System.out.println(flashcardsDeleted > 0
                        ? "Deleted " + flashcardsDeleted + " flashcards for user " + userId
                        : "No flashcards to delete for user " + userId);
            }

            try (PreparedStatement ps3 = conn.prepareStatement(deletePromptsSql)) {
                ps3.setInt(1, userId);
                int count3 = ps3.executeUpdate();
                System.out.println(count3 > 0
                        ? "Deleted " + count3 + " prompts for user " + userId
                        : "No prompts to delete for user " + userId);
            }

            conn.commit();
            System.out.println("Note reset complete for user " + userId);

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            showAlert(Alert.AlertType.ERROR,
                    "Data Error",
                    "Failed to reset notes and flashcards: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Could not reset auto-commit: " + ex.getMessage());
            }
        }
    }

    /**
     * Displays the profile settings pane, retrieves the current user's email from the database,
     * and populates the email field and a masked password field.
     */
    @FXML
    private void showProfileSettings() {
        ProfileSettingsContent.setVisible(true);

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

    /**
     * Displays a JavaFX alert dialog with the specified type, title, and message.
     *
     * @param type    the type of alert (e.g., INFORMATION, ERROR)
     * @param title   the title of the alert window
     * @param message the content message to display in the alert
     */
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

    /**
     * Checks whether the given email ends with one of the supported domains.
     *
     * @param email the email address to validate
     * @return true if the email ends with a supported domain, false otherwise
     */
    private boolean isDomainValid(String email) {
        for (String domain : validDomains) {
            if (email.toLowerCase().endsWith(domain)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves changes to the current user's profile. Validates email format and domain,
     * checks for email uniqueness, and conditionally updates only the email or both
     * email and password in the database. Shows alerts for validation errors or success.
     */
    @FXML
    private void saveChanges() {
        String newEmail = emailField.getText().trim().toLowerCase();
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

    /**
     * Hides the profile settings pane and returns to the previous view.
     */
    @FXML
    private void goBack() {
        ProfileSettingsContent.setVisible(false);
    }
}
