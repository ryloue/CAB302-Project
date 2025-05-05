package com.example.cab302finalproj;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

class Navigator {
    public static MainLayout mainLayout;
}

public class Sidebar {

    private static final Logger LOGGER = Logger.getLogger(Sidebar.class.getName());

    @FXML
    private void handleHomeClick() {
        Navigator.mainLayout.loadPage("Home.fxml");
    }

    @FXML
    private void handleDashboardClick() {
        Navigator.mainLayout.loadPage("Dashboard.fxml");
    }

    @FXML
    private void handleFlashcardsClick() {
        Navigator.mainLayout.loadPage("Flashcards.fxml");
    }

    @FXML
    private void handleHistoryClick() {
        Navigator.mainLayout.loadPage("History.fxml");
    }

    @FXML
    private void handleSettingsClick() {
        Navigator.mainLayout.loadPage("Settings.fxml");
    }

    @FXML
    private void handleLogoutClick() {
        try {
            // Clear the content area in MainLayout
            Navigator.mainLayout.loadPage(null); // Reset the content area

            // Load the Login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cab302finalproj/Login.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) Navigator.mainLayout.contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during logout", e);
        }
    }
}
