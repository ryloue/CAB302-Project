package com.example.cab302finalproj;

import com.example.cab302finalproj.controller.History;
import com.example.cab302finalproj.model.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Main layout controller that manages page navigation and content loading.
 * Serves as the primary container for the application's pages.
 */
public class MainLayout {

    @FXML
    StackPane contentArea;

    @FXML
    private void initialize() {
        Navigator.mainLayout = this;

        DatabaseManager.getInstance().getConnection();

        loadPage("Home.fxml");
    }

    /**
     * Loads a specified FXML page into the content area.
     * Handles special initialization for certain pages like History.
     *
     * @param page the FXML filename to load (e.g., "Home.fxml"), or null to clear content
     * @throws RuntimeException if a SQL exception occurs during database operations
     */
    public void loadPage(String page) {
        try {
            if (page == null) {
                contentArea.getChildren().clear();
                return;
            }

            String currentPage = page;

            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cab302finalproj/" + page));
            contentArea.getChildren().add(loader.load());

            if (currentPage.equals("History.fxml")) {
                if (DatabaseManager.getInstance().getConnection() == null ||
                        DatabaseManager.getInstance().getConnection().isClosed()) {
                    DatabaseManager.getInstance();
                }

                History controller = loader.getController();
                controller.refreshData();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}