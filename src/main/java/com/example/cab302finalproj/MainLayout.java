package com.example.cab302finalproj;

import com.example.cab302finalproj.database.IAccountDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import com.example.cab302finalproj.database.sqLiteAccountDAO;
import java.io.IOException;
import java.util.Objects;

public class MainLayout {
    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Load the Home page by default
        Navigator.mainLayout = this;
        loadPage("Home.fxml");
    }

    public void loadPage(String fxml) {
        try {
            Parent page = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
