package com.example.cab302finalproj;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainLayout {

    @FXML
    StackPane contentArea;

    @FXML
    private void initialize() {
        Navigator.mainLayout = this;
        loadPage("Home.fxml");
    }

    public void loadPage(String page) {
        try {
            if (page == null) {
                contentArea.getChildren().clear();
                return;
            }
            contentArea.getChildren().clear();
            contentArea.getChildren().add(FXMLLoader.load(getClass().getResource("/com/example/cab302finalproj/" + page)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}