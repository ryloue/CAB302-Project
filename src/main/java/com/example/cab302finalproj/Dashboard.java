package com.example.cab302finalproj;

import com.example.cab302finalproj.model.CurrentUser;
import com.example.cab302finalproj.model.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import javafx.scene.text.Text;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Dashboard {

    private Button createButtonWithRenameSupport(String label) {
        Button button = new Button(label);
        button.setStyle("-fx-background-color: #FDC500; -fx-background-radius: 10;");

        // When clicked once: update SELECTED and load notes
        button.setOnAction(event -> {
            selectedLabel.setText("SELECTED: " + button.getText());
            currentlySelectedButton = button;
            String notes = loadNotesFromDatabase(button.getText());
            notesArea.setText(notes);  // Display the loaded notes
        });

        // When double-clicked: rename
        button.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                renameButton(button, event);
            }
        });

        return button;
    }

    private void saveButtonToDatabase(String label) {
        int userId = CurrentUser.getCurrentUserId();

        String insertSQL = "INSERT INTO DashNotes (userId, label) VALUES (?, ?)";

        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(insertSQL)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, label);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving DashNote: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private TextArea notesArea;

    @FXML
    private Button addMoreButton;

    @FXML
    private TextField searchField;

    @FXML
    private HBox buttonContainer;

    private int buttonCount = 1;

    @FXML
    public void handleAddMore() {
        int existingButtons = buttonContainer.getChildren().size() - 1;

        if (existingButtons >= 7) {
            System.out.println("Maximum of 7 buttons reached.");
            return;
        }

        String buttonLabel = "Button " + buttonCount++;
        Button newButton = createButtonWithRenameSupport(buttonLabel);

        buttonContainer.getChildren().add(newButton);

        // Save to database
        saveButtonToDatabase(buttonLabel);
    }

    private void renameButton(Button button, MouseEvent event) {
        TextField renamer = new TextField(button.getText());
        renamer.setPrefWidth(button.getWidth());

        int index = buttonContainer.getChildren().indexOf(button);
        buttonContainer.getChildren().set(index, renamer);

        renamer.setOnAction(e -> {
            String newLabel = renamer.getText();
            updateButtonLabelInDatabase(button.getText(), newLabel);
            button.setText(newLabel);
            buttonContainer.getChildren().set(index, button);
            selectedLabel.setText("SELECTED: " + newLabel);
        });

        renamer.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String newLabel = renamer.getText();
                updateButtonLabelInDatabase(button.getText(), newLabel);
                button.setText(newLabel);
                buttonContainer.getChildren().set(index, button);
                selectedLabel.setText("SELECTED: " + newLabel);
            }
        });

        renamer.requestFocus();
    }

    private void updateButtonLabelInDatabase(String oldLabel, String newLabel) {
        int userId = CurrentUser.getCurrentUserId();

        String updateSQL = "UPDATE DashNotes SET label = ? WHERE userId = ? AND label = ?";

        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(updateSQL)) {
            pstmt.setString(1, newLabel);
            pstmt.setInt(2, userId);
            pstmt.setString(3, oldLabel);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating DashNote label: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        loadUserDashNotes();
    }

    private void loadUserDashNotes() {
        int userId = CurrentUser.getCurrentUserId();

        String query = "SELECT label FROM DashNotes WHERE userId = ?";

        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String label = rs.getString("label");
                Button button = createButtonWithRenameSupport(label);
                buttonContainer.getChildren().add(button);
            }
        } catch (SQLException e) {
            System.err.println("Error loading DashNotes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private Text selectedLabel;

    private Button currentlySelectedButton = null;

    @FXML
    public void handleDeleteSelected() {
        if (currentlySelectedButton == null) {
            System.out.println("No button selected.");
            return;
        }

        String labelToDelete = currentlySelectedButton.getText();
        int userId = CurrentUser.getCurrentUserId();

        // Remove from UI
        buttonContainer.getChildren().remove(currentlySelectedButton);

        // Remove from database
        String deleteSQL = "DELETE FROM DashNotes WHERE userId = ? AND label = ?";

        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(deleteSQL)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, labelToDelete);
            pstmt.executeUpdate();
            System.out.println("Deleted: " + labelToDelete);
        } catch (SQLException e) {
            System.err.println("Error deleting DashNote: " + e.getMessage());
        }

        // Reset selection
        currentlySelectedButton = null;
        selectedLabel.setText("SELECTED: ");
    }

    @FXML
    private void saveNotesToDatabase(String buttonLabel, String notes) {
        int userId = CurrentUser.getCurrentUserId();
        String updateSQL = "UPDATE DashNotes SET notes = ? WHERE userId = ? AND label = ?";

        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(updateSQL)) {
            pstmt.setString(1, notes);
            pstmt.setInt(2, userId);
            pstmt.setString(3, buttonLabel);
            pstmt.executeUpdate();
            System.out.println("Notes saved for button: " + buttonLabel);
        } catch (SQLException e) {
            System.err.println("Error saving notes: " + e.getMessage());
        }
    }

    @FXML
    public void handleSaveNotes() {
        String buttonLabel = selectedLabel.getText().replace("SELECTED: ", "");
        String notes = notesArea.getText();

        if (!buttonLabel.isEmpty()) {
            saveNotesToDatabase(buttonLabel, notes);
        } else {
            System.out.println("No button selected to save notes.");
        }
    }

    private String loadNotesFromDatabase(String buttonLabel) {
        int userId = CurrentUser.getCurrentUserId();

        String query = "SELECT notes FROM DashNotes WHERE userId = ? AND label = ?";

        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, buttonLabel);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("notes");
            }
        } catch (SQLException e) {
            System.err.println("Error loading notes from database: " + e.getMessage());
            e.printStackTrace();
        }
        return ""; // Return empty if no notes are found
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();

        if (query.isEmpty()) {
            selectedLabel.setText("SELECTED: ");
            currentlySelectedButton = null;
            return;
        }

        Button bestMatch = null;

        for (javafx.scene.Node node : buttonContainer.getChildren()) {
            if (node instanceof Button button) {
                String label = button.getText().toLowerCase();

                // Check for startsWith match first
                if (label.startsWith(query)) {
                    bestMatch = button;
                    break;
                }

                // If not found, check for contains match
                if (bestMatch == null && label.contains(query)) {
                    bestMatch = button;
                }
            }
        }

        if (bestMatch != null) {
            // Set the selected button and update the label
            selectedLabel.setText("SELECTED: " + bestMatch.getText());
            currentlySelectedButton = bestMatch;
            notesArea.setText(loadNotesFromDatabase(bestMatch.getText()));
        }
    }
}